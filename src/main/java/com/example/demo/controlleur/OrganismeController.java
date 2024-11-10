package com.example.demo.controlleur;

import com.example.demo.entité.Image;
import com.example.demo.entité.Module;
import com.example.demo.entité.Organisme;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.ImageService;
import com.example.demo.service.ModuleService;
import com.example.demo.service.OrganismeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organismes")
public class OrganismeController {

    @Autowired
    private OrganismeService organismeService;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ImageService imageService;
    @Autowired
    private ModuleService moduleService;

    @PostMapping
    public ResponseEntity<String> addOrganisme(@RequestParam("nomOrganisme") String nomOrganisme,@RequestParam("adresseOrganisme") String adresseOrganisme,
                                          @RequestParam("photo") MultipartFile photo,@RequestParam("numeroOrganisme") long numeroOrganisme,
                                               @RequestParam("moduleId") Long moduleId) throws IOException {
        // Vérifiez si l'image est vide ou non
        if (!photo.isEmpty()) {
            // Upload de l'image sur Cloudinary
            Map result = cloudinaryService.upload(photo);
            String photoUrl = (String) result.get("url");

            // Créez un nouvel objet Image
            Image image = new Image();
            image.setName(photo.getOriginalFilename());
            image.setImageUrl(photoUrl);
            image.setImageId((String) result.get("public_id"));
            imageService.save(image);

            // Créez un nouvel objet User avec le nom et l'image associée
            Organisme organisme = new Organisme();
            organisme.setNomOrganisme(nomOrganisme);
            organisme.setImage(image);
            organisme.setAdresseOrganisme(adresseOrganisme);
            organisme.setNumeroOrganisme(numeroOrganisme);
            //organismeService.saveOrganisme(organisme);
            Organisme savedOrganisme = organismeService.addOrganismeWithModule(organisme, moduleId);


            return new ResponseEntity<>("Organisme ajouté avec succès !", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Veuillez sélectionner une image.", HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/OsM")
    public ResponseEntity<String> saveOrganisme(@RequestParam("nomOrganisme") String nomOrganisme,
                                                @RequestParam("adresseOrganisme") String adresseOrganisme,
                                                @RequestParam("photo") MultipartFile photo,
                                                @RequestParam("numeroOrganisme") long numeroOrganisme
                                                ) throws IOException {
        if (!photo.isEmpty()) {
            // Upload de l'image sur Cloudinary
            Map result = cloudinaryService.upload(photo);
            String photoUrl = (String) result.get("url");

            // Créez un nouvel objet Image
            Image image = new Image();
            image.setName(photo.getOriginalFilename());
            image.setImageUrl(photoUrl);
            image.setImageId((String) result.get("public_id"));
            imageService.save(image);


            Organisme oorganisme = new Organisme();
            oorganisme.setNomOrganisme(nomOrganisme);
            oorganisme.setImage(image);
            oorganisme.setAdresseOrganisme(adresseOrganisme);
            oorganisme.setNumeroOrganisme(numeroOrganisme);
            oorganisme.setModules(null);
            organismeService.saveOrganisme(oorganisme);


            return new ResponseEntity<>("Organisme ajouté avec succès !", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Veuillez sélectionner une image.", HttpStatus.BAD_REQUEST);
        }    }

    @GetMapping
    public List<Organisme> getAllOrganismes() {
        return organismeService.getAllOrganismes();
    }

    @PostMapping("/addWithModule")
    public ResponseEntity<Organisme> addOrganismeWithModule(@RequestBody Organisme organisme, @RequestParam Long moduleId) {
        Organisme savedOrganisme = organismeService.addOrganismeWithModule(organisme, moduleId);
        return ResponseEntity.ok(savedOrganisme);
    }

    @PostMapping("/{organismeId}/modules/{moduleId}")
    public ResponseEntity<?> addModuleToOrganisme(@PathVariable Long organismeId, @PathVariable Long moduleId) {
        Organisme organisme = organismeService.findById(organismeId);
        Module module = moduleService.findById(moduleId);

        if (organisme == null || module == null) {
            return ResponseEntity.badRequest().body("Organisme or Module not found");
        }

        organisme.getModules().add(module);
        organismeService.save(organisme);

        return ResponseEntity.ok().body("Module added to Organisme successfully");
    }

    @DeleteMapping("/{organismeId}")
    public ResponseEntity<Void> deleteOrganisme(@PathVariable long organismeId) {
        organismeService.deleteOrganisme(organismeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/module")
    public ResponseEntity<List<Organisme>> getOrganismesByModule(@RequestParam Module module) {
        List<Organisme> organismes = organismeService.getOrganismesByModule(module);
        return ResponseEntity.ok().body(organismes);
    }
}
