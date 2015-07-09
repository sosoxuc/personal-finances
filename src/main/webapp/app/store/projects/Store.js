Ext.define('TR.store.projects.Store', {
    extend : 'Ext.data.Store',
    storeId: 'projectsStore',
    autoLoad: true,
    fields: ['id', 'projectName'],
    proxy: {
    	url: 'rest/project/list',
    	type: 'rest',
    	reader: {
    		type: 'json'
    	}
    }
});