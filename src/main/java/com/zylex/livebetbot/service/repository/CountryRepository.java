package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Country;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Set;

@Repository
public class CountryRepository {

    private Session session;

    @PostConstruct
    private void postConstruct() {
        session = HibernateUtil.getSession();
    }

    @PreDestroy
    private void preDestroy() {
        if (session.isOpen()) {
            session.close();
        }
    }

    @Transactional
    public void save(Country country) {
        Country retreatedCountry = get(country);
        if (retreatedCountry.getName() == null) {
            session.beginTransaction();
            Long id = (Long) session.save(country);
            country.setId(id);
            session.getTransaction().commit();
        } else {
            country.setId(retreatedCountry.getId());
        }
    }

    @Transactional
    public void save(Set<Country> countries) {
        countries.forEach(this::save);
    }

    @Transactional
    public Country get(Country country) {
        Query query = session.createQuery("FROM Country WHERE name = :countryName");
        query.setParameter("countryName", country.getName());
        try {
            return (Country) query.getSingleResult();
        } catch (NoResultException e) {
            return new Country();
        }
    }
}