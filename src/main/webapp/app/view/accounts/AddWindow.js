Ext.define("TR.view.accounts.AddWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 400,
    height: 350,
    autoShow : true,
    layout : 'border',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.title = cfg.edit ? LANG.EDIT : LANG.ADD;
        var form = Ext.create('Ext.form.Panel', {
            border: false,
            split: true,
            region: 'center',
            bodyPadding: 5,
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%'
            },
            defaultType : 'textfield',
            items : [ {
                fieldLabel : LANG.NAME,
                name : 'accountName',
                value: cfg.data ? cfg.data.accountName : '',
                allowBlank: false
            }, {
                fieldLabel : LANG.NUMBER,
                name : 'accountNumber',
                value: cfg.data ? cfg.data.accountNumber : '',
                allowBlank: true
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
                form.getForm().findField('accountsName').focus(true, 10);
            }
        });
        
        function add() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            
            myRequest({
                url : 'rest/account/create',
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
                url : 'rest/account/update',
                params : values,
                callback : function(id) {
                    cfg.grid.store.load();
                    me.close();
                }
            });
        }
    }
});