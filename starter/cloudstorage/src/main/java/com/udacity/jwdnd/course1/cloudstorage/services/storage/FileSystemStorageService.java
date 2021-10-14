package com.udacity.jwdnd.course1.cloudstorage.services.storage;

import com.udacity.jwdnd.course1.cloudstorage.config.StoragePropertiesConfig;
import com.udacity.jwdnd.course1.cloudstorage.exception.StorageException;
import com.udacity.jwdnd.course1.cloudstorage.exception.StorageFileNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.stream.Stream;


@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    private MessageSource messageSource;

    public FileSystemStorageService(StoragePropertiesConfig properties, MessageSource messageSource) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.messageSource = messageSource;
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException(messageSource.getMessage("could_not_initialize_storage", null, Locale.ENGLISH), e);
        }
    }

    @Override
    public void store(MultipartFile file) {

        try {

            if (file.isEmpty()) {
                throw new StorageException(messageSource.getMessage("empty_file_failed", null, Locale.ENGLISH));
            }

            // Normalise destination File Path
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(messageSource.getMessage("current_dir_store_file_failed", null, Locale.ENGLISH));
            }

            // try-with-resources
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new StorageException(messageSource.getMessage("store_file_failed", null, Locale.ENGLISH), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException(messageSource.getMessage("file_read_failed", null, Locale.ENGLISH), e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(messageSource.getMessage("file_read_failed", new Object[]{filename}, Locale.ENGLISH));
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException(messageSource.getMessage("no_file_selected_to_upload", new Object[]{filename}, Locale.ENGLISH), e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

}
