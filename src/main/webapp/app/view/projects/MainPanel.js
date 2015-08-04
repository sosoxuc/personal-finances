Ext.define("TR.view.projects.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    layout: 'border',
    title : LANG.PROJECTS,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var grid = Ext.create('TR.view.projects.ProjectsGrid', {
            region: 'center'
        });
        
        me.items = [ grid ];

        me.callParent(arguments);
    }
});