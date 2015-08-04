Ext.define("TR.view.accounts.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    layout: 'border',
    title : LANG.ACCOUNTS,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var grid = Ext.create('TR.view.accounts.AccountsGrid', {
            region: 'center'
        });
        
        me.items = [ grid ];

        me.callParent(arguments);
    }
});