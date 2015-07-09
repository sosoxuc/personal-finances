Ext.define('TR.view.transactions.MainPanel', {
    extend : 'Ext.panel.Panel',
    border: false,
    layout: 'border',
    title : 'ტრანზაქციები',
    defaults: {
        split: true
    },
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        var transactionGrid = Ext.create('TR.view.transactions.TransactionGrid',{
            region: 'center',
            employeeId: cfg.employeeId,
            startDate: cfg.startDate,
            endDate: cfg.endDate,
            border: true
        });
        
        var searchForm = Ext.create('TR.view.transactions.SearchForm', {
            region: 'north',
            employeeId: cfg.employeeId,
            startDate: cfg.startDate,
            endDate: cfg.endDate,
            grid: transactionGrid,
            split: false
        });
        
        me.items = [ searchForm, transactionGrid];

        me.callParent(arguments);
        
        searchForm.filter();
    }
});