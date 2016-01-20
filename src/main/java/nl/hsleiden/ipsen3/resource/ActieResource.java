package nl.hsleiden.ipsen3.resource;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.hibernate.UnitOfWork;
import nl.hsleiden.ipsen3.core.Actie;
import nl.hsleiden.ipsen3.core.Bestellijst;
import nl.hsleiden.ipsen3.dao.ActieDAO;
import nl.hsleiden.ipsen3.dao.BestellijstDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Thijs Koek on 1/20/2016.
 */
public class ActieResource {
    private final String template;
    private final String defaultName;
    private final ActieDAO dao;


    public ActieResource(String template, String defaultName, ActieDAO dao){
        this.template = template;
        this.defaultName = defaultName;
        this.dao = dao;
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    @UnitOfWork
    public long createActie(Actie actie){
        return dao.create(actie);
    }

    @GET
    @Timed
    @UnitOfWork
    public List<Actie> findAll(){
        return dao.findAll();
    }

    @GET
    @Timed
    @UnitOfWork
    @Path("/{id}")
    public Actie getById(@PathParam("id") Long id) {
        return dao.findById(id);
    }
}