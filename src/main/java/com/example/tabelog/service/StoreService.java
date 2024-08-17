package com.example.tabelog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabelog.entity.Store;
import com.example.tabelog.form.StoreEditForm;
import com.example.tabelog.form.StoreRegisterForm;
import com.example.tabelog.repository.StoreRepository;

@Service
public class StoreService {
	private final StoreRepository storeRepository;
	
	public StoreService(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}
	
	@Transactional
	public void create(StoreRegisterForm storeRegisterForm) {
		Store store = new Store();
		MultipartFile imageFile = storeRegisterForm.getImageFile();
		
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile,filePath);
			store.setImageName(hashedImageName);
		}
		
		store.setName(storeRegisterForm.getName());
		store.setDescription(storeRegisterForm.getDescription());
		
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
		        .append(DateTimeFormatter.ISO_LOCAL_TIME)
		        .parseDefaulting(ChronoField.EPOCH_DAY, 0)
		        .toFormatter();
		LocalDateTime openingTime = LocalDateTime.parse(storeRegisterForm.getOpeningTime(), fmt);
		LocalDateTime closingTime = LocalDateTime.parse(storeRegisterForm.getClosingTime(), fmt);
		
		store.setOpeningTime(openingTime);
		store.setClosingTime(closingTime);
		store.setPostalCode(storeRegisterForm.getPostalCode());
		store.setAddress(storeRegisterForm.getAddress());
		store.setPhoneNumber(storeRegisterForm.getPhoneNumber());
		store.setPriceMax(storeRegisterForm.getPriceMax());
		store.setPriceMin(storeRegisterForm.getPriceMin());
		
		storeRepository.save(store);

	}
	
	@Transactional
	public void update(StoreEditForm storeEditForm) {
		Store store = storeRepository.getReferenceById(storeEditForm.getId());
		MultipartFile imageFile = storeEditForm.getImageFile();
		
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resourses/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			store.setImageName(hashedImageName);
		}
		
		store.setName(storeEditForm.getName());
		store.setDescription(storeEditForm.getDescription());
		
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
		        .append(DateTimeFormatter.ISO_LOCAL_TIME)
		        .parseDefaulting(ChronoField.EPOCH_DAY, 0)
		        .toFormatter();
		LocalDateTime openingTime = LocalDateTime.parse(storeEditForm.getOpeningTime(), fmt);
		LocalDateTime closingTime = LocalDateTime.parse(storeEditForm.getClosingTime(), fmt);
		
		store.setOpeningTime(openingTime);
		store.setClosingTime(closingTime);
		store.setPostalCode(storeEditForm.getPostalCode());
		store.setAddress(storeEditForm.getAddress());
		store.setPhoneNumber(storeEditForm.getPhoneNumber());
		store.setPriceMax(storeEditForm.getPriceMax());
		store.setPriceMin(storeEditForm.getPriceMin());
		
		storeRepository.save(store);
	}

	private String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
			
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}
	
	private void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
