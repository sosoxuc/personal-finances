Ext.define('TR.store.directions.Store', {
    extend : 'Ext.data.Store',
    storeId: 'directionsStore',
    fields: [
        {name: 'label'},
        {name: 'value', type: 'int'}
    ], 
    data: [
        {label: LANG.EXPENCE, value: -1},
        {label: LANG.INCOME, value: 1}
    ]
});