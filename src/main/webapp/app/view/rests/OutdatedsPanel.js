Ext.define("TR.view.rests.OutdatedsPanel", {
    extend : "Ext.panel.Panel",
    defaultType : 'textfield',
    title : LANG.ACCEPT_REJECT_TRANSACTIONS,
    buttonAlign : 'left',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        var grid = Ext.create("TR.view.rests.OutdatedsGrid");

        me.items = [ grid ];

        me.callParent(arguments);

    }
});