package fr.semifir.apicinema;

import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.controllers.SalleController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.dtos.salle.SalleDTO;
import fr.semifir.apicinema.entities.Cinema;
import fr.semifir.apicinema.entities.Salle;
import fr.semifir.apicinema.services.CinemaService;
import fr.semifir.apicinema.services.SalleService;
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

@WebMvcTest(controllers = SalleController.class)
public class SalleControllerTest {

    // On injecte le MockMVCpour simuler un composant (component)
    @Autowired
    private MockMvc mockMvc;

    // On mock le service que l'on récupère et on le copie
    @MockBean
    SalleService service;

    @Test
    public void testFindAllCinemas() throws Exception {
        this.mockMvc.perform(get("/salles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testWrongSalleOrWrongId() throws Exception {
        this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testOneSalle() throws Exception {
        SalleDTO salleDTO = this.salleDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salleDTO));

        MvcResult result = this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();

        SalleDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                SalleDTO.class
        );
        Assertions.assertEquals(body.getId(), this.salleDTO().getId());
        Assertions.assertEquals(body.getNumDeSalle(), this.salleDTO().getNumDeSalle());
        Assertions.assertEquals(body.getNbrPlace(), this.salleDTO().getNbrPlace());
        Assertions.assertEquals(body.getCinema(), this.salleDTO().getCinema());
    }


    @Test
    public void testSaveSalleDTO() throws Exception {
        SalleDTO salleDTO = this.salleDTO();
        Gson json = new GsonBuilder().create();
        String body = json.toJson(salleDTO);
        this.mockMvc.perform(post("/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

    }

    @Test
    public void testUpdateSalleDTO() throws Exception {
        SalleDTO salleDTO = this.salleDTO();
        SalleDTO salleDTOUpdate = this.salleDTOUpdate();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(salleDTO));

        MvcResult result = this.mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        SalleDTO body = json.fromJson(result.getResponse().getContentAsString(), SalleDTO.class);

        BDDMockito.when(service.save(any(Salle.class)))
                .thenReturn(salleDTOUpdate);


        body.setNbrPlace(120);
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(get("/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                .andExpect(status().isOk())
                .andReturn();

        SalleDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), SalleDTO.class);
        Assertions.assertEquals(finalBody.getNbrPlace(), this.salleDTOUpdate().getNbrPlace());

    }

    @Test
    public void testDeleteSalle() throws Exception {
        Gson json = new GsonBuilder().create();
        String body = json.toJson(this.salleDTO());
        this.mockMvc.perform(delete("/salles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }


    private SalleDTO salleDTO() {
        Cinema cinema = new Cinema();
        return new SalleDTO("1", 4, 90, cinema);
    }

    private SalleDTO salleDTOUpdate() {
        Cinema cinema = new Cinema();
        return new SalleDTO("1", 8, 120, cinema);
    }

}
