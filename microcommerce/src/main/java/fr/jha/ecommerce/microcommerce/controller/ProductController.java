package fr.jha.ecommerce.microcommerce.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import fr.jha.ecommerce.microcommerce.dao.ProductDao;
import fr.jha.ecommerce.microcommerce.exceptions.EntityNotFoundException;
import fr.jha.ecommerce.microcommerce.exceptions.ProduitGratuitException;
import fr.jha.ecommerce.microcommerce.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@Api(description = "API pour les opérations CRUD sur le sproduits")
@RestController
public class ProductController {
    @Autowired
    private ProductDao productDao;

    // FindAll
    @ApiOperation(value = "Récupére la liste de tous les produits")
    @GetMapping(value = "/Produits")
    public MappingJacksonValue listeProduits(){
        Iterable<Product> products = productDao.findAll();
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listFilter = new SimpleFilterProvider().addFilter("filter", filter);
        MappingJacksonValue productsFilter = new MappingJacksonValue(products);
        productsFilter.setFilters(listFilter);
        return productsFilter;
    }

    // FindById
    @ApiOperation(value = "Récupère un produit par son ID à condition que celui-ci soit en stock")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) throws EntityNotFoundException {

        Product product = productDao.findById(id);

        if (product == null)throw new EntityNotFoundException("Le produit avec l'id "+id+" n'existe pas");
        return product;
    }

    // Create product
    @ApiOperation(value = "Créer un nouveau produit à condition qu'il soit valide")
    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) throws ProduitGratuitException {
        Product newProduct =  new Product();
        newProduct.setNom(product.getNom());
        newProduct.setPrix(product.getPrix());
        newProduct.setPrixAchat(product.getPrixAchat());

        Product productAdded =  productDao.save(newProduct);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        if (productAdded.getPrix() == 0)throw new ProduitGratuitException("Le prix de vente ne peut pas être de 0");

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // Delete product
    @DeleteMapping(value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id){
        productDao.deleteById(id);
    }

    // Update product
    @PutMapping(value = "/Produits")
    public void updateProduit(@RequestBody Product product){
        productDao.save(product);
    }

    // Margin calculation
    @GetMapping(value = "/AdminProduits")
    public Map<Product, Integer> calculerMargeProduit(){
        List<Product> products = productDao.findAll();
        HashMap<Product, Integer> map = new HashMap<Product, Integer>();
        for (Product product : products){
            int marge = product.getPrix() - product.getPrixAchat();
            map.put(product, marge);
        }
        return map;
    }

    // Trier par ordre alphabétique
    @GetMapping(value = "/TriProduits")
    public List<Product> trierProduitsParOrdreAlphabetique(){
        List<Product> products = productDao.findAllByOrderByNom();
        return products;
    }
}
