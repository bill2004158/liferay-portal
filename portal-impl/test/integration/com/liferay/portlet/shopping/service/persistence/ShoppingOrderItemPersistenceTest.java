/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.shopping.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.BasePersistence;
import com.liferay.portal.service.persistence.PersistenceExecutionTestListener;
import com.liferay.portal.test.LiferayPersistenceIntegrationJUnitTestRunner;
import com.liferay.portal.test.persistence.test.TransactionalPersistenceAdvice;
import com.liferay.portal.util.test.RandomTestUtil;

import com.liferay.portlet.shopping.NoSuchOrderItemException;
import com.liferay.portlet.shopping.model.ShoppingOrderItem;
import com.liferay.portlet.shopping.service.ShoppingOrderItemLocalServiceUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
@ExecutionTestListeners(listeners =  {
	PersistenceExecutionTestListener.class})
@RunWith(LiferayPersistenceIntegrationJUnitTestRunner.class)
public class ShoppingOrderItemPersistenceTest {
	@Before
	public void setUp() {
		_modelListeners = _persistence.getListeners();

		for (ModelListener<ShoppingOrderItem> modelListener : _modelListeners) {
			_persistence.unregisterListener(modelListener);
		}
	}

	@After
	public void tearDown() throws Exception {
		Map<Serializable, BasePersistence<?>> basePersistences = _transactionalPersistenceAdvice.getBasePersistences();

		Set<Serializable> primaryKeys = basePersistences.keySet();

		for (Serializable primaryKey : primaryKeys) {
			BasePersistence<?> basePersistence = basePersistences.get(primaryKey);

			try {
				basePersistence.remove(primaryKey);
			}
			catch (Exception e) {
				if (_log.isDebugEnabled()) {
					_log.debug("The model with primary key " + primaryKey +
						" was already deleted");
				}
			}
		}

		_transactionalPersistenceAdvice.reset();

		for (ModelListener<ShoppingOrderItem> modelListener : _modelListeners) {
			_persistence.registerListener(modelListener);
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingOrderItem shoppingOrderItem = _persistence.create(pk);

		Assert.assertNotNull(shoppingOrderItem);

		Assert.assertEquals(shoppingOrderItem.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		_persistence.remove(newShoppingOrderItem);

		ShoppingOrderItem existingShoppingOrderItem = _persistence.fetchByPrimaryKey(newShoppingOrderItem.getPrimaryKey());

		Assert.assertNull(existingShoppingOrderItem);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addShoppingOrderItem();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingOrderItem newShoppingOrderItem = _persistence.create(pk);

		newShoppingOrderItem.setOrderId(RandomTestUtil.nextLong());

		newShoppingOrderItem.setItemId(RandomTestUtil.randomString());

		newShoppingOrderItem.setSku(RandomTestUtil.randomString());

		newShoppingOrderItem.setName(RandomTestUtil.randomString());

		newShoppingOrderItem.setDescription(RandomTestUtil.randomString());

		newShoppingOrderItem.setProperties(RandomTestUtil.randomString());

		newShoppingOrderItem.setPrice(RandomTestUtil.nextDouble());

		newShoppingOrderItem.setQuantity(RandomTestUtil.nextInt());

		newShoppingOrderItem.setShippedDate(RandomTestUtil.nextDate());

		_persistence.update(newShoppingOrderItem);

		ShoppingOrderItem existingShoppingOrderItem = _persistence.findByPrimaryKey(newShoppingOrderItem.getPrimaryKey());

		Assert.assertEquals(existingShoppingOrderItem.getOrderItemId(),
			newShoppingOrderItem.getOrderItemId());
		Assert.assertEquals(existingShoppingOrderItem.getOrderId(),
			newShoppingOrderItem.getOrderId());
		Assert.assertEquals(existingShoppingOrderItem.getItemId(),
			newShoppingOrderItem.getItemId());
		Assert.assertEquals(existingShoppingOrderItem.getSku(),
			newShoppingOrderItem.getSku());
		Assert.assertEquals(existingShoppingOrderItem.getName(),
			newShoppingOrderItem.getName());
		Assert.assertEquals(existingShoppingOrderItem.getDescription(),
			newShoppingOrderItem.getDescription());
		Assert.assertEquals(existingShoppingOrderItem.getProperties(),
			newShoppingOrderItem.getProperties());
		AssertUtils.assertEquals(existingShoppingOrderItem.getPrice(),
			newShoppingOrderItem.getPrice());
		Assert.assertEquals(existingShoppingOrderItem.getQuantity(),
			newShoppingOrderItem.getQuantity());
		Assert.assertEquals(Time.getShortTimestamp(
				existingShoppingOrderItem.getShippedDate()),
			Time.getShortTimestamp(newShoppingOrderItem.getShippedDate()));
	}

	@Test
	public void testCountByOrderId() {
		try {
			_persistence.countByOrderId(RandomTestUtil.nextLong());

			_persistence.countByOrderId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		ShoppingOrderItem existingShoppingOrderItem = _persistence.findByPrimaryKey(newShoppingOrderItem.getPrimaryKey());

		Assert.assertEquals(existingShoppingOrderItem, newShoppingOrderItem);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail("Missing entity did not throw NoSuchOrderItemException");
		}
		catch (NoSuchOrderItemException nsee) {
		}
	}

	@Test
	public void testFindAll() throws Exception {
		try {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected OrderByComparator getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("ShoppingOrderItem",
			"orderItemId", true, "orderId", true, "itemId", true, "sku", true,
			"name", true, "description", true, "properties", true, "price",
			true, "quantity", true, "shippedDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		ShoppingOrderItem existingShoppingOrderItem = _persistence.fetchByPrimaryKey(newShoppingOrderItem.getPrimaryKey());

		Assert.assertEquals(existingShoppingOrderItem, newShoppingOrderItem);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingOrderItem missingShoppingOrderItem = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingShoppingOrderItem);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		ShoppingOrderItem newShoppingOrderItem1 = addShoppingOrderItem();
		ShoppingOrderItem newShoppingOrderItem2 = addShoppingOrderItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newShoppingOrderItem1.getPrimaryKey());
		primaryKeys.add(newShoppingOrderItem2.getPrimaryKey());

		Map<Serializable, ShoppingOrderItem> shoppingOrderItems = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, shoppingOrderItems.size());
		Assert.assertEquals(newShoppingOrderItem1,
			shoppingOrderItems.get(newShoppingOrderItem1.getPrimaryKey()));
		Assert.assertEquals(newShoppingOrderItem2,
			shoppingOrderItems.get(newShoppingOrderItem2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ShoppingOrderItem> shoppingOrderItems = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(shoppingOrderItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newShoppingOrderItem.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ShoppingOrderItem> shoppingOrderItems = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, shoppingOrderItems.size());
		Assert.assertEquals(newShoppingOrderItem,
			shoppingOrderItems.get(newShoppingOrderItem.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ShoppingOrderItem> shoppingOrderItems = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(shoppingOrderItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newShoppingOrderItem.getPrimaryKey());

		Map<Serializable, ShoppingOrderItem> shoppingOrderItems = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, shoppingOrderItems.size());
		Assert.assertEquals(newShoppingOrderItem,
			shoppingOrderItems.get(newShoppingOrderItem.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = ShoppingOrderItemLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod() {
				@Override
				public void performAction(Object object) {
					ShoppingOrderItem shoppingOrderItem = (ShoppingOrderItem)object;

					Assert.assertNotNull(shoppingOrderItem);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrderItem.class,
				ShoppingOrderItem.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("orderItemId",
				newShoppingOrderItem.getOrderItemId()));

		List<ShoppingOrderItem> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		ShoppingOrderItem existingShoppingOrderItem = result.get(0);

		Assert.assertEquals(existingShoppingOrderItem, newShoppingOrderItem);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrderItem.class,
				ShoppingOrderItem.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("orderItemId",
				RandomTestUtil.nextLong()));

		List<ShoppingOrderItem> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		ShoppingOrderItem newShoppingOrderItem = addShoppingOrderItem();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrderItem.class,
				ShoppingOrderItem.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("orderItemId"));

		Object newOrderItemId = newShoppingOrderItem.getOrderItemId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("orderItemId",
				new Object[] { newOrderItemId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOrderItemId = result.get(0);

		Assert.assertEquals(existingOrderItemId, newOrderItemId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingOrderItem.class,
				ShoppingOrderItem.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("orderItemId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("orderItemId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ShoppingOrderItem addShoppingOrderItem()
		throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingOrderItem shoppingOrderItem = _persistence.create(pk);

		shoppingOrderItem.setOrderId(RandomTestUtil.nextLong());

		shoppingOrderItem.setItemId(RandomTestUtil.randomString());

		shoppingOrderItem.setSku(RandomTestUtil.randomString());

		shoppingOrderItem.setName(RandomTestUtil.randomString());

		shoppingOrderItem.setDescription(RandomTestUtil.randomString());

		shoppingOrderItem.setProperties(RandomTestUtil.randomString());

		shoppingOrderItem.setPrice(RandomTestUtil.nextDouble());

		shoppingOrderItem.setQuantity(RandomTestUtil.nextInt());

		shoppingOrderItem.setShippedDate(RandomTestUtil.nextDate());

		_persistence.update(shoppingOrderItem);

		return shoppingOrderItem;
	}

	private static Log _log = LogFactoryUtil.getLog(ShoppingOrderItemPersistenceTest.class);
	private ModelListener<ShoppingOrderItem>[] _modelListeners;
	private ShoppingOrderItemPersistence _persistence = (ShoppingOrderItemPersistence)PortalBeanLocatorUtil.locate(ShoppingOrderItemPersistence.class.getName());
	private TransactionalPersistenceAdvice _transactionalPersistenceAdvice = (TransactionalPersistenceAdvice)PortalBeanLocatorUtil.locate(TransactionalPersistenceAdvice.class.getName());
}