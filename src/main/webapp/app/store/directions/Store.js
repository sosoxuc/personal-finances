Ext.define('TR.store.directions.Store', {
    extend : 'Ext.data.Store',
    storeId: 'directionsStore',
    fields: [
        {name: 'label'},
        {name: 'value', type: 'int'}
    ], 
    data: [
        {label: 'გასავალი', value: -1},
        {label: 'შემოსავალი', value: 1}
    ]
});