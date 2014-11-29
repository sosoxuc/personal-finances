package personal.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

/**
 * To work with JPA utilities
 * 
 * @author Levan Tsinadze
 * 
 */
public class EntityMapper {

    /**
     * Gets {@link javax.perisistence.Id} annotated field / fields name by
     * entity class
     * 
     * @param em
     * @param entityClass
     * @return {@link String}
     */
    public static String getIdField(EntityManager em, Class<?> entityClass) {

	String name;

	EntityType<?> entityType = em.getMetamodel().entity(entityClass);
	Class<?> idClass = entityType.getIdType().getJavaType();
	SingularAttribute<?, ?> attribute = entityType.getDeclaredId(idClass);
	name = attribute.getName();

	return name;
    }

    /**
     * Gets {@link javax.perisistence.Id} annotated field / fields value by
     * entity instance
     * 
     * @param emf
     * @param entity
     * @return {@link Object}
     */
    public static Object getIdValue(EntityManagerFactory emf, Object entity) {
	return emf.getPersistenceUnitUtil().getIdentifier(entity);
    }

    /**
     * Gets {@link javax.perisistence.Id} annotated field / fields value by
     * entity instance
     * 
     * @param em
     * @param entity
     * @return
     */
    public static Object getIdValue(EntityManager em, Object entity) {
	return getIdValue(em.getEntityManagerFactory(), entity);
    }
}
