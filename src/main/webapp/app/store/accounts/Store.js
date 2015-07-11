Ext.define('TR.store.accounts.Store', {
    extend : 'Ext.data.Store',
    storeId: 'accountsStore',
    autoLoad: true,
    fields: ['id', 'accountName'],
    proxy: {
    	url: 'rest/account/list',
    	type: 'rest',
    	reader: {
    		type: 'json'
    	}
    }
});