Ext.define('TR.store.currencies.Store', {
    extend : 'Ext.data.Store',
    storeId: 'currenciesStore',
    autoLoad: true,
    fields: ['id', 'currencyName'],
    proxy: {
    	url: 'rest/currency/list',
    	type: 'rest',
    	reader: {
    		type: 'json'
    	}
    }
});