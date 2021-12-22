package fr.semifir.apicinema;

import fr.semifir.apicinema.controllers.CinemaController;
import fr.semifir.apicinema.dtos.cinema.CinemaDTO;
import fr.semifir.apicinema.services.CinemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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


    }

    private CinemaDTO cinemaDTO() {
        return new CinemaDTO(
            "1",
            "Kinepolis"
        );
    }


}
