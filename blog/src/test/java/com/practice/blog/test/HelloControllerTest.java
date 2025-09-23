package com.practice.blog.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
@MockBean(JpaMetamodelMappingContext.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void return_hello() throws Exception {

        //Given
        String expectedResponse = "Hello World";

        //When & Then
        mockMvc.perform(get("/helloworld"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}