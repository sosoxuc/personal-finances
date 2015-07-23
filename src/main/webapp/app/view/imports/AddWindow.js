Ext.define("TR.view.imports.AddWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 400,
    height: 350,
    autoShow : true,
    layout : 'border',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.title = cfg.edit ? 'რედაქტირება' : 'დამატება';
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            split: true,
            region: 'center',
            bodyPadding: 5,
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%',
                allowBlank: false
            },
            defaultType : 'textfield',
            items : [ {
                fieldLabel : 'დასახელება',
                name : 'importName',
                value: cfg.data ? cfg.data.projectName : ''
            }]
        });
        

        me.items = [ form ];

        me.buttons = [{
            text : cfg.edit ? 'რედაქტირება' : 'დამატება',
            handler : cfg.edit ? edit : add
        }];
        
        me.callParent(arguments);

        me.on({
            show: function(formPanel, options) {
                form.getForm().findField('importName').focus(true, 10);
            }
        });
        
        function add() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            
            myRequest({
                url : 'rest/import/create',
                params : values,
                callback : function(id) {
                    cfg.grid.store.load();
                    cfg.grid.getSelectionModel().deselectAll();
                    me.close();
                }
            });
        }

        function edit() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            values.id = cfg.data.id;
            values.version = cfg.data.version;
            myRequest({
                url : 'rest/import/update',
                params : values,
                callback : function(id) {
                    cfg.grid.store.load();
                    cfg.grid.getSelectionModel().deselectAll();
                    me.close();
                }
            });
        }
    }
});