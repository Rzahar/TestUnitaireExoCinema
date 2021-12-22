package fr.semifir.apicinema;

import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.services.CinemaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CinemaController.class)
public class CinemaControllerTest {

    // On injecte le MockMVCpour simuler un composant (component)
    @Autowired
    private MockMvc mockMvc;

    // On mock le service que l'on récupère et on le copie
    @MockBean
    CinemaService service;

    @Test
    public void testFindAllCinemas() throws Exception{
        this.mockMvc.perform(get("/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testWrongCinemaOrWrongId() throws Exception{
    this.mockMvc.perform(get("/cinemas/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testOneCinema() throws Exception {
        CinemaDTO cinemaDTO = this.cinemaDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(cinemaDTO));

        MvcResult result =this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();

        CinemaDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                CinemaDTO.class
                        );
        Assertions.assertEquals(body.getId(), this.cinemaDTO().getId());
        Assertions.assertEquals(body.getNom(), this.cinemaDTO().getNom());
    }

    @Test
    public void testSaveCinemaDTO() throws Exception{
        CinemaDTO cinemaDTO = this.cinemaDTO();
        Gson json = new GsonBuilder().create();
        String body = json.toJson(cinemaDTO);
        this.mockMvc.perform(post("/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

    }

    @Test
    public void testUpdateCinemaDTO()throws Exception{
        CinemaDTO cinemaDTO = this.cinemaDTO();
        CinemaDTO cinemaDTOUpdate = this.cinemaDTOUpdate();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(cinemaDTO));

        MvcResult result = this.mockMvc.perform(get("/cinemas/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        CinemaDTO body = json.fromJson(result.getResponse().getContentAsString(), CinemaDTO.class);

        BDDMockito.when(service.save(any(Cinema.class)))
                .thenReturn(cinemaDTOUpdate); //


        body.setNom("Gaumont");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(get("/cinemas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyToSave))
                .andExpect(status().isOk())
                .andReturn();

        CinemaDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), CinemaDTO.class);
        Assertions.assertEquals(finalBody.getNom(), this.cinemaDTOUpdate().getNom());

        }

    @Test
    public void testDeleteCinema() throws Exception{
        Gson json = new GsonBuilder().create();
        String body = json.toJson(this.cinemaDTO());
        this.mockMvc.perform(delete("/cinemas")
                        .contentType(MediaType.APPLICATION_JSON) // Le type de données que l'on passe dans notre request
                        .content(body)) // Le contenu du body
                .andExpect(status().isOk());
    }



    private CinemaDTO cinemaDTO() {
        return new CinemaDTO(
            "1",
            "Kinepolis"
        );
    }

    private CinemaDTO cinemaDTOUpdate() {
        return new CinemaDTO(
                "1",
                "UGC"
        );
    }


}
