package com.example.rpialmanac;

import com.example.rpialmanac.PMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "addmarkerendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath = "rpialmanac"))
public class AddmarkerEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listaddMarker")
	public CollectionResponse<addMarker> listaddMarker(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<addMarker> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery(addMarker.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<addMarker>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (addMarker obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<addMarker> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getaddMarker")
	public addMarker getaddMarker(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		addMarker addmarker = null;
		try {
			addmarker = mgr.getObjectById(addMarker.class, id);
		} finally {
			mgr.close();
		}
		return addmarker;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param addmarker the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertaddMarker")
	public addMarker insertaddMarker(addMarker addmarker) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsaddMarker(addmarker)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(addmarker);
		} finally {
			mgr.close();
		}
		return addmarker;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param addmarker the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateaddMarker")
	public addMarker updateaddMarker(addMarker addmarker) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsaddMarker(addmarker)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(addmarker);
		} finally {
			mgr.close();
		}
		return addmarker;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeaddMarker")
	public void removeaddMarker(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			addMarker addmarker = mgr.getObjectById(addMarker.class, id);
			mgr.deletePersistent(addmarker);
		} finally {
			mgr.close();
		}
	}

	private boolean containsaddMarker(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			addMarker addmarker = mgr.getObjectById(addMarker.class, id);
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
