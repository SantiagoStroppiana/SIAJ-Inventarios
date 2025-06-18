package siaj.inventarios.controller;

import siaj.inventarios.model.GrupoCategoria;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import siaj.inventarios.service.GrupoCategoriaService;
import siaj.inventarios.service.GrupoCategoriaServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GrupoCategoriaController {

    private GrupoCategoriaService grupoCategoriaService;
    private ObjectMapper objectMapper;

    public GrupoCategoriaController(GrupoCategoriaService grupoCategoriaService) {
        this.grupoCategoriaService = grupoCategoriaService;
        this.objectMapper = new ObjectMapper();
    }

    // Obtener todos los grupos
    public void getAllGrupos(Context ctx) {
        try {
            System.out.println("Antes de llamar al servicio");
            List<GrupoCategoria> grupos = grupoCategoriaService.findAll();
            System.out.println("Después de llamar al servicio, grupos obtenidos: " + grupos.size());

            // Verificar si las categorías están cargadas
            for (GrupoCategoria grupo : grupos) {
                System.out.println("Grupo: " + grupo.getNombre() + ", Categorías: " + grupo.getCategorias().size());
            }

            System.out.println("Antes de serializar a JSON");
            ctx.json(grupos);
            System.out.println("Después de serializar a JSON");
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el stack trace completo
            ctx.status(500).json(Map.of("error", "Error al obtener grupos: " + e.getMessage()));
        }
    }

    // Obtener todos los grupos con sus categorías
    public void getAllGruposWithCategorias(Context ctx) {
        try {
            List<GrupoCategoria> grupos = grupoCategoriaService.findAllWithCategorias();
            ctx.json(grupos).status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al obtener grupos con categorías: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener grupo por ID
    public void getGrupoById(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            Optional<GrupoCategoria> grupo = grupoCategoriaService.findById(id);

            if (grupo.isPresent()) {
                ctx.json(grupo.get()).status(HttpStatus.OK);
            } else {
                ctx.json(new ErrorResponse("Grupo no encontrado"))
                        .status(HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            ctx.json(new ErrorResponse("ID inválido"))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al obtener grupo: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener grupo por ID con categorías
    public void getGrupoByIdWithCategorias(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            Optional<GrupoCategoria> grupo = grupoCategoriaService.findByIdWithCategorias(id);

            if (grupo.isPresent()) {
                ctx.json(grupo.get()).status(HttpStatus.OK);
            } else {
                ctx.json(new ErrorResponse("Grupo no encontrado"))
                        .status(HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            ctx.json(new ErrorResponse("ID inválido"))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al obtener grupo: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar por nombre
    public void searchByNombre(Context ctx) {
        try {
            String nombre = ctx.queryParam("nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                ctx.json(new ErrorResponse("Parámetro 'nombre' requerido"))
                        .status(HttpStatus.BAD_REQUEST);
                return;
            }

            List<GrupoCategoria> grupos = grupoCategoriaService.findByNombreContaining(nombre);
            ctx.json(grupos).status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error en búsqueda: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar por descripción
    public void searchByDescripcion(Context ctx) {
        try {
            String descripcion = ctx.queryParam("descripcion");
            if (descripcion == null || descripcion.trim().isEmpty()) {
                ctx.json(new ErrorResponse("Parámetro 'descripcion' requerido"))
                        .status(HttpStatus.BAD_REQUEST);
                return;
            }

            List<GrupoCategoria> grupos = grupoCategoriaService.findByDescripcionContaining(descripcion);
            ctx.json(grupos).status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error en búsqueda: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Crear nuevo grupo
    public void createGrupo(Context ctx) {
        try {
            GrupoCategoria grupoCategoria = ctx.bodyAsClass(GrupoCategoria.class);
            GrupoCategoria savedGrupo = grupoCategoriaService.save(grupoCategoria);
            ctx.json(savedGrupo).status(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ctx.json(new ErrorResponse("Datos inválidos: " + e.getMessage()))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Ya existe")) {
                ctx.json(new ErrorResponse(e.getMessage()))
                        .status(HttpStatus.CONFLICT);
            } else {
                ctx.json(new ErrorResponse("Error al crear grupo: " + e.getMessage()))
                        .status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al crear grupo: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar grupo existente
    public void updateGrupo(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            GrupoCategoria grupoCategoria = ctx.bodyAsClass(GrupoCategoria.class);

            GrupoCategoria updatedGrupo = grupoCategoriaService.update(id, grupoCategoria);
            ctx.json(updatedGrupo).status(HttpStatus.OK);
        } catch (NumberFormatException e) {
            ctx.json(new ErrorResponse("ID inválido"))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            ctx.json(new ErrorResponse("Datos inválidos: " + e.getMessage()))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                ctx.json(new ErrorResponse(e.getMessage()))
                        .status(HttpStatus.NOT_FOUND);
            } else if (e.getMessage().contains("Ya existe")) {
                ctx.json(new ErrorResponse(e.getMessage()))
                        .status(HttpStatus.CONFLICT);
            } else {
                ctx.json(new ErrorResponse("Error al actualizar grupo: " + e.getMessage()))
                        .status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al actualizar grupo: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar grupo
    public void deleteGrupo(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            grupoCategoriaService.deleteById(id);
            ctx.status(HttpStatus.NO_CONTENT);
        } catch (NumberFormatException e) {
            ctx.json(new ErrorResponse("ID inválido"))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                ctx.json(new ErrorResponse(e.getMessage()))
                        .status(HttpStatus.NOT_FOUND);
            } else if (e.getMessage().contains("categorías asociadas")) {
                ctx.json(new ErrorResponse(e.getMessage()))
                        .status(HttpStatus.CONFLICT);
            } else {
                ctx.json(new ErrorResponse("Error al eliminar grupo: " + e.getMessage()))
                        .status(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al eliminar grupo: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Verificar si existe por nombre
    public void existsByNombre(Context ctx) {
        try {
            String nombre = ctx.pathParam("nombre");
            boolean exists = grupoCategoriaService.existsByNombre(nombre);
            ctx.json(new ExistsResponse(exists)).status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al verificar existencia: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Verificar si existe por ID
    public void existsById(Context ctx) {
        try {
            Integer id = Integer.parseInt(ctx.pathParam("id"));
            boolean exists = grupoCategoriaService.existsById(id);
            ctx.json(new ExistsResponse(exists)).status(HttpStatus.OK);
        } catch (NumberFormatException e) {
            ctx.json(new ErrorResponse("ID inválido"))
                    .status(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al verificar existencia: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener conteo total
    public void countGrupos(Context ctx) {
        try {
            long count = grupoCategoriaService.count();
            ctx.json(new CountResponse(count)).status(HttpStatus.OK);
        } catch (Exception e) {
            ctx.json(new ErrorResponse("Error al obtener conteo: " + e.getMessage()))
                    .status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Clases de respuesta
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class ExistsResponse {
        private boolean exists;

        public ExistsResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }

    public static class CountResponse {
        private long count;

        public CountResponse(long count) {
            this.count = count;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}