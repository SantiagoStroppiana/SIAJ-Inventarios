package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.CategoriaController;
import siaj.inventarios.controller.GrupoCategoriaController;
import siaj.inventarios.service.GrupoCategoriaServiceImpl;

public class RutasGrupoCategoria {

    private GrupoCategoriaController grupoCategoriaController;
    public RutasGrupoCategoria() {}
    public RutasGrupoCategoria(GrupoCategoriaController grupoCategoriaController) {
        this.grupoCategoriaController = grupoCategoriaController;
    }



    public void rutaGrupoCategoria(Javalin app) {




        // Rutas para grupos de categorías
            // CRUD básico
            app.get("/api/grupos-categorias", grupoCategoriaController::getAllGrupos);
            app.get("/api/grupos-categorias/with-categorias", grupoCategoriaController::getAllGruposWithCategorias);
            app.get("/api/grupos-categorias/{id}", grupoCategoriaController::getGrupoById);
            app.get("/api/grupos-categorias/{id}/with-categorias", grupoCategoriaController::getGrupoByIdWithCategorias);
            app.post("/api/grupos-categorias", grupoCategoriaController::createGrupo);
            app.put("/api/grupos-categorias/{id}", grupoCategoriaController::updateGrupo);
            app.delete("/api/grupos-categorias/{id}", grupoCategoriaController::deleteGrupo);

            // Búsquedas
            app.get("/api/grupos-categorias/search/nombre", grupoCategoriaController::searchByNombre);
            app.get("/api/grupos-categorias/search/descripcion", grupoCategoriaController::searchByDescripcion);

            // Utilidades
            app.get("/api/grupos-categorias/exists/nombre/{nombre}", grupoCategoriaController::existsByNombre);
            app.get("/api/grupos-categorias/exists/{id}", grupoCategoriaController::existsById);
            app.get("/api/grupos-categorias/count", grupoCategoriaController::countGrupos);
    }
}
