Ext.define('TR.store.employees.WorkplaceStore', {
	extend : 'Ext.data.Store',
	storeId: 'workplaceStore',
	fields: ['id', 'workplaceName'],
	autoLoad: true,
	proxy: {
		type: 'rest',
		url: 'rest/hr/workplace/list',
		reader: {
			type: 'json'
		}
	}
});