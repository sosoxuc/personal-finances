Ext.define("TR.view.currencies.AddWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 400,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.title = cfg.edit ? LANG.EDIT : LANG.ADD;
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            bodyPadding: 5,
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%',
                allowBlank: false
            },
            defaultType : 'textfield',
            items : [ {
                fieldLabel : LANG.NAME,
                name : 'currencyName',
                value: cfg.data ? cfg.data.currencyName : ''
            }, {
                fieldLabel : LANG.CODE,
                name : 'currencyCode',
                value: cfg.data ? cfg.data.currencyCode : ''
            }]
        });
        

        me.items = [ form ];

        me.buttons = [{
            text : cfg.edit ? LANG.EDIT : LANG.ADD,
            handler : cfg.edit ? edit : add
        }];
        
        me.callParent(arguments);

        me.on({
            show: function(formPanel, options) {
                form.getForm().findField('currencyName').focus(true, 10);
            }
        });
        
        function add() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            
            myRequest({
                url : 'rest/currency/create',
                params : values,
                callback : function(id) {
                    cfg.grid.store.load();
                    cfg.grid.getSelectionModel().deselectAll();
                    cfg.grid.down('button[name=edit]').disable();
                    cfg.grid.down('button[name=remove]').disable();
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
                url : 'rest/currency/update',
                params : values,
                callback : function(id) {
                    cfg.grid.store.load();
                    me.close();
                }
            });
        }
    }
});