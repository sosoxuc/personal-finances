Ext.define("TR.view.imports.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    layout: 'border',
    title : 'იმპორტი',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var grid = Ext.create('TR.view.imports.ImportsGrid', {
            region: 'center'
        });
        
        me.items = [ grid ];

        me.callParent(arguments);
    }
});