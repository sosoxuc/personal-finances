Ext.define("TR.view.imports.ImportsGrid", {
    extend : "Ext.grid.Panel",
    border : false,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        me.tbar = [ {
            text : LANG.UPLOAD,
            name : 'upload',
            handler : upload
        } ];

        me.columns = [ {
            header : LANG.NAME,
            dataIndex : 'fileName',
            flex : 1
        } ];

        me.callParent(arguments);

        function upload() {
            Ext.create('TR.view.imports.UploadWindow', {
                grid : me
            });
        }
    }
});