package fi.metatavu.edelphi.dao.resources;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.metatavu.edelphi.dao.GenericDAO;
import fi.metatavu.edelphi.domainmodel.resources.Resource;
import fi.metatavu.edelphi.domainmodel.resources.ResourceLock;
import fi.metatavu.edelphi.domainmodel.resources.ResourceLock_;
import fi.metatavu.edelphi.domainmodel.users.User;

@ApplicationScoped
public class ResourceLockDAO extends GenericDAO<ResourceLock> {
  
  public ResourceLock create(Resource resource, User creator, Date expires) {
    Date created = new Date();
    
    ResourceLock resourceLock = new ResourceLock();
    resourceLock.setCreated(created);
    resourceLock.setCreator(creator);
    resourceLock.setExpires(expires);
    resourceLock.setResource(resource);
    
    getEntityManager().persist(resourceLock);
    
    return resourceLock;
  }
  
  public ResourceLock findByResource(Resource resource) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ResourceLock> criteria = criteriaBuilder.createQuery(ResourceLock.class);
    Root<ResourceLock> root = criteria.from(ResourceLock.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(ResourceLock_.resource), resource)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public ResourceLock updateExpires(ResourceLock resourceLock, Date expires) {
    EntityManager entityManager = getEntityManager();
    resourceLock.setExpires(expires);
    entityManager.persist(resourceLock);
    return resourceLock;
  }

}
