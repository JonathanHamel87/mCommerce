package fr.jha.ecommerce.microcommerce.dao;

import fr.jha.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

    List<Product> findByPrixGreaterThan(int prix);

    Product findById(int id);

    List<Product> findAllByOrderByNom();

    @Query("SELECT p.id, p.nom, p.prix FROM Product p WHERE p.prix > :prixLimit")
    List<Product> chercherUnProduitCher(@Param("prixLimit") int prix);
}
