Ext.define("TR.view.employees.AddWindow", {
    extend : "Ext.window.Window",
    modal : true,
    width : 400,
    autoShow : true,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.title = cfg.edit ? LANG.EDIT : LANG.ADD;

        var positionStore = Ext.StoreManager.lookup('positionStore') || Ext.create('TR.store.employees.PositionStore');
        var workplaceStore = Ext.StoreManager.lookup('workplaceStore') || Ext.create('TR.store.employees.WorkplaceStore');

        var workplaceCombo = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel : LANG.WORKPLACE,
            name : 'workplaceId',
            store : workplaceStore,
            queryMode : 'local',
            flex : 1,
            editable : false,
            displayField : 'workplaceName',
            valueField : 'id'
        });

        var positionCombo = Ext.create('Ext.form.field.ComboBox', {
            store : positionStore,
            editable : false,
            queryMode : 'local',
            flex : 1,
            displayField : 'positionName',
            valueField : 'id',
            fieldLabel : LANG.POSITION,
            name : 'positionId'
        });

        var form = Ext.create('Ext.form.Panel', {
            border : false,
            bodyPadding : 5,
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%'
            },
            defaultType : 'textfield',
            items : [ {
                fieldLabel : LANG.LASTNAME,
                name : 'lastName',
                allowBlank : false
            }, {
                fieldLabel : LANG.FIRSTNAME,
                name : 'firstName',
                allowBlank : false
            }, {
                xtype : 'datefield',
                fieldLabel : LANG.BIRTHDATE,
                name : 'birthDate',
                emptyText : 'dd-mm-yyyy',
                format: 'd-m-Y',
            }, {
                fieldLabel : LANG.PERSONAL_NUMBER,
                name : 'personalNo',
                isEng : true
            }, {
                xtype : 'fieldcontainer',
                layout : 'hbox',
                items : [ positionCombo, {
                    xtype : 'splitter'
                }, {
                    xtype : 'button',
                    text : '+',
                    handler : addPosition
                } ]
            }, {
                xtype : 'fieldcontainer',
                layout : 'hbox',
                items : [ workplaceCombo, {
                    xtype : 'splitter'
                }, {
                    xtype : 'button',
                    text : '+',
                    handler : addWorkplace
                } ]
            }, {
                fieldLabel : LANG.PHONE,
                name : 'phone'
            }, {
                fieldLabel : LANG.EMAIL,
                vtype : 'email',
                name : 'email',
                isEng : true
            }, {
                fieldLabel : LANG.USERNAME,
                name : 'username',
                isEng : true
            }, {
                fieldLabel : LANG.PASSWORD,
                name : 'password',
                isEng : true
            } ]
        });

        me.items = [ form ];

        me.buttons = [];

        me.buttons.push({
            text : cfg.edit ? LANG.EDIT : LANG.ADD,
            handler : cfg.edit ? edit : add
        });

        me.callParent(arguments);
        
        if(cfg.data) {
            me.down('form').getForm().setValues(cfg.data);
        }

        function add() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            for (var property in values) {
                if (values.hasOwnProperty(property)) {
                    if (!values[property] || values[property]==='') {
                        delete values[property]
                    }
                }
            }
            myRequest({
                url : 'rest/hr/employee/add',
                params : values,
                callback : function(id) {
                    cfg.searchForm.search();
                    me.close();
                }
            });
        }

        function edit() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
                values.id = cfg.data.id;
            for (var property in values) {
                if (values.hasOwnProperty(property)) {
                    if (!values[property] || values[property]==='') {
                        delete values[property]
                    }
                }
            }

            myRequest({
                url : 'rest/hr/employee/update',
                params : values,
                callback : function(id) {
                    cfg.searchForm.search();
                    me.close();
                }
            });
        }

        function addPosition() {
            Ext.Msg.prompt(LANG.ADD, LANG.POSITION, function(ans, text) {
                if (ans == 'ok' && text) {
                    myRequest({
                        url : 'rest/hr/position/add',
                        params : {
                            name : text
                        },
                        callback : function(data) {
                            positionStore.load();
                            positionCombo.setValue(data.id);
                        }
                    });
                }
            });
        }

        function addWorkplace() {
            Ext.Msg.prompt(LANG.ADD, LANG.WORKPLACE,
                function(ans, text) {
                    if (ans == 'ok' && text) {
                        myRequest({
                            url : 'rest/hr/workplace/add',
                            params : {
                                name : text
                            },
                            callback : function(data) {
                                workplaceStore.load();
                                workplaceCombo.setValue(data.id);
                            }
                        });
                    }
                });
        }
    }
});