Ext.define('TR.store.intervals.Store', {
    extend : 'Ext.data.Store',
    storeId: 'intervalsStore',
    fields: [
        {name: 'label'},
        {name: 'value', type: 'int'}
    ], 
    data: [
        {label: LANG.DAY, value: 1},
        {label: LANG.WEEK, value: 4},
        {label: LANG.MONTH, value: 2},
        {label: LANG.YEAR, value: 3},
    ]
});