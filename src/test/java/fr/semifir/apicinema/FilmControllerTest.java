package fr.semifir.apicinema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.semifir.apicinema.controllers.FilmController;
import fr.semifir.apicinema.dtos.film.FilmDTO;
import fr.semifir.apicinema.entities.Film;
import fr.semifir.apicinema.entities.Seance;
import fr.semifir.apicinema.services.FilmService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {

    // On injecte le MockMVCpour simuler un composant (component)
    @Autowired
    private MockMvc mockMvc;

    // On mock le service que l'on récupère et on le copie
    @MockBean
    FilmService service;

    @Test
    public void testFindAllFilms() throws Exception {
        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testWrongFilmOrWrongId() throws Exception {
        this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testOneFilm() throws Exception {
        FilmDTO filmDTO = this.filmDTO();
        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(filmDTO));

        MvcResult result = this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();

        FilmDTO body = json.fromJson(
                result.getResponse().getContentAsString(),
                FilmDTO.class
        );
        Assertions.assertEquals(body.getId(), this.filmDTO().getId());
        Assertions.assertEquals(body.getNom(), this.filmDTO().getNom());
        Assertions.assertEquals(body.getDuree(), this.filmDTO().getDuree());
        Assertions.assertEquals(body.getSeance(), this.filmDTO().getSeance());
    }


    @Test
    public void testSaveFilmDTO() throws Exception {
        FilmDTO filmDTO = this.filmDTO();
        Gson json = new GsonBuilder().create();
        String body = json.toJson(filmDTO);
        this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

    }

    @Test
    public void testUpdateFilmDTO() throws Exception {
        FilmDTO filmDTO = this.filmDTO();
        FilmDTO filmDTOUpdate = this.filmDTOUpdate();

        BDDMockito.given(service.findByID("1"))
                .willReturn(Optional.of(filmDTO));

        MvcResult result = this.mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().create();
        FilmDTO body = json.fromJson(result.getResponse().getContentAsString(), FilmDTO.class);

        BDDMockito.when(service.save(any(Film.class)))
                .thenReturn(filmDTOUpdate); //


        body.setNom("Spiderman");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                .andExpect(status().isOk())
                .andReturn();

        FilmDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), FilmDTO.class);
        Assertions.assertEquals(finalBody.getNom(), this.filmDTOUpdate().getNom());

    }


    @Test
    public void testDeleteFilm() throws Exception {
        Gson json = new GsonBuilder().create();
        String body = json.toJson(this.filmDTO());
        this.mockMvc.perform(delete("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    private FilmDTO filmDTO() {
        Seance seance = new Seance();
        return new FilmDTO(
                "1", "Idiocracy", 124F, seance);
    }

    private FilmDTO filmDTOUpdate() {
        Seance seance = new Seance();
        return new FilmDTO(
                "1", "Matrix 4", 136F, seance);
    }
}
