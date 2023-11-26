package de.muellermarius.controller;

import de.muellermarius.model.entities.Meal;
import de.muellermarius.service.FoodClassificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
public class FoodClassificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoodClassificationController.class);

    private final FoodClassificationService foodClassificationService;

    public FoodClassificationController(final FoodClassificationService foodClassificationService) {
        this.foodClassificationService = foodClassificationService;
    }


    @PostMapping("/api/v1/food/detect")
    public ResponseEntity<Meal> detectFoodOnImage(@RequestParam("file") MultipartFile image) {
        try {
            final String originalFilename = image.getOriginalFilename();
            final byte[] bytes = image.getBytes();

            final String base64Image = Base64.getEncoder().encodeToString(bytes);

            Meal meal = foodClassificationService.startFoodDetectionProcess(base64Image, originalFilename);

            return ResponseEntity.status(HttpStatus.OK).body(meal);
        } catch (IOException e) {
            LOGGER.error("Fehler beim lesen des Images: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
