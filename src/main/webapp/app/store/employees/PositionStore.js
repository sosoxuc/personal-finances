Ext.define('TR.store.employees.PositionStore', {
	extend : 'Ext.data.Store',
	storeId: 'positionStore',
	fields: ['id', 'positionName'],
	autoLoad: true,
	proxy: {
		type: 'rest',
		url: 'rest/hr/position/list',
		reader: {
			type: 'json'
		}
	}
});