package com.example.rpialmanac;

import com.example.rpialmanac.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "Addmarkerendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath = "rpialmanac"))
public class Addmarkerendpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listAddmarker")
	public CollectionResponse<Addmarker> listAddmarker(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Addmarker> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Addmarker as marker");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				//HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				//extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				//query.setExtensions(extensionMap);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0); 
				query.setMaxResults(limit);
			}

			execute = (List<Addmarker>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Addmarker obj : execute);
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Addmarker>builder()
				.setItems(execute).setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getAddmarker")
	public Addmarker getAddmarker(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Addmarker Addmarker = null;
		try {
			Addmarker = mgr.find(Addmarker.class, id);
		} finally {
			mgr.close();
		}
		return Addmarker;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param Addmarker the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertAddmarker")
	public Addmarker insertAddmarker(Addmarker Addmarker) {
		EntityManager mgr = getEntityManager();
		try {
			/**
			if (containsAddmarker(Addmarker)) {
				throw new EntityExistsException("Object already exists");
			}
			*/
			mgr.persist(Addmarker);
		} finally {
			mgr.close();
		}
		return Addmarker;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param Addmarker the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateAddmarker")
	public Addmarker updateAddmarker(Addmarker Addmarker) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsAddmarker(Addmarker)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(Addmarker);
		} finally {
			mgr.close();
		}
		return Addmarker;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeAddmarker")
	public void removeAddmarker(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Addmarker Addmarker = mgr.find(Addmarker.class, id);
			mgr.remove(Addmarker);
		} finally {
			mgr.close();
		}
	}

	private boolean containsAddmarker(Addmarker Addmarker) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Addmarker marker = mgr.find(Addmarker.class, Addmarker.getKey());
			if (marker == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
