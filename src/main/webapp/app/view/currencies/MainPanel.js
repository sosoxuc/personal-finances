Ext.define("TR.view.currencies.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    layout: 'border',
    title : LANG.CURRENCIES,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var grid = Ext.create('TR.view.currencies.CurrenciesGrid', {
            region: 'center'
        });
        
        me.items = [ grid ];

        me.callParent(arguments);
    }
});