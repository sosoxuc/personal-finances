Ext.define("TR.view.employees.EmployeeGrid", {
    extend : "Ext.grid.Panel",
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.store = Ext.StoreManager.lookup('employeeStore')
                || Ext.create('TR.store.employees.EmployeeStore');

        me.tbar = [ {
            text : 'დამატება',
            name : 'add',
            handler : add
        }, {
            text : 'რედაქტირება',
            name : 'edit',
            handler : edit
        }, {
            text : 'გაუქმება',
            name : 'disable',
            handler : disable
        }, {
            text : 'გააქტიურება',
            name : 'enable',
            handler : enable
        }, {
            text : 'წაშლა',
            name : 'remove',
            handler : remove
        } ];

        me.columns = [ {
            header : 'პირადი ნომერი',
            dataIndex : 'personalNo',
            flex : 1
        }, {
            header : 'გვარი, სახელი',
            dataIndex : 'fullName',
            flex : 1
        }, {
            header : 'დაბ. თარიღი',
            dataIndex : 'birthDate',
            flex : 1,
            renderer : Ext.util.Format.dateRenderer('d/m/Y')
        }, {
            header : 'სამუშაო ადგილი',
            dataIndex : 'workplace',
            flex : 1
        }, {
            header : 'პოზიცია',
            dataIndex : 'position',
            flex : 1
        }, {
            header : 'ტელეფონი',
            dataIndex : 'phone',
            flex : 1,
            hidden : true
        }, {
            header : 'ელ. ფოსტა',
            dataIndex : 'email',
            flex : 1,
            hidden : true
        }, {
            header : 'ტიპი',
            dataIndex : 'type',
            flex : 1,
        }, {
            header : 'სტატუსი',
            dataIndex : 'stateName',
            flex : 1
        }, {
            header : 'ანაზღაურება',
            dataIndex : 'hourlySalary',
            flex : 1
        }, {
            header : 'ვადის გასვლის თარ.',
            dataIndex : 'expireDate',
            flex : 1,
            renderer : Ext.util.Format.dateRenderer('d/m/Y')
        } ];

        me.callParent(arguments);

        me.on('itemdblclick', edit);

        me.on('select', changeButtons);

        function add() {
            Ext.create('TR.view.employees.AddWindow', {
                grid : me,
                searchForm : me.searchForm
            });
        }

        function edit() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            var win = Ext.create('TR.view.employees.AddWindow', {
                grid : me,
                edit : true,
                employeeId : sel[0].get('id'),
                searchForm : me.searchForm
            });
            var values = sel[0].getData();

            values.userRole = values.userRole == 2 ? 1 : 0;

            win.down('form').getForm().setValues(values);
        }

        function disable() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            Ext.Msg.confirm('ყურადღება', 'დაადასტურეთ ჩანაწერის გაუქმება!',
                    function(ans) {
                        if (ans == 'yes') {
                            myRequest({
                                url : 'rest/employee/disable',
                                params : {
                                    id : sel[0].get('id')
                                },
                                callback : function(res) {
                                    me.searchForm.search();
                                }
                            });
                        }
                    });
        }

        function enable() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            Ext.Msg.confirm('ყურადღება', 'დაადასტურეთ ჩანაწერის გააქტიურება!',
                    function(ans) {
                        if (ans == 'yes') {
                            myRequest({
                                url : 'rest/employee/enable',
                                params : {
                                    id : sel[0].get('id')
                                },
                                callback : function(res) {
                                    me.searchForm.search();
                                }
                            });
                        }
                    });
        }

        function remove() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            Ext.Msg.confirm('ყურადღება', 'დაადასტურეთ ჩანაწერის წაშლა!',
                    function(ans) {
                        if (ans == 'yes') {
                            myRequest({
                                url : 'rest/employee/remove',
                                params : {
                                    id : sel[0].get('id')
                                },
                                callback : function(res) {
                                    me.searchForm.search();
                                }
                            });
                        }
                    });
        }

        function resetButtons() {
            me.btns = me.btns || {};
            if (!me.btns.edit)
                me.btns.edit = me.down('button[name=edit]');
            if (!me.btns.disable)
                me.btns.disable = me.down('button[name=disable]');
            if (!me.btns.enable)
                me.btns.enable = me.down('button[name=enable]');
            if (!me.btns.remove)
                me.btns.remove = me.down('button[name=remove]');
            if (!me.btns.log)
                me.btns.log = me.down('button[name=log]');

            me.btns.edit.disable();
            me.btns.disable.disable();
            me.btns.enable.disable();
            me.btns.remove.disable();
            me.btns.log.disable();
        }

        function changeButtons(view, rec, ind) {
            if (!me.btns)
                resetButtons();

            var state = rec.get('state');
            me.btns.edit.enable();
            me.btns.log.enable();
            if (state == 1) {
                me.btns.enable.disable();
                me.btns.disable.enable();
                me.btns.remove.enable();
            } else if (state == 2) {
                me.btns.enable.enable();
                me.btns.disable.enable();
                me.btns.remove.disable();
            } else if (state == 0) {
                me.btns.enable.enable();
                me.btns.disable.disable();
                me.btns.remove.enable();
            }
        }

        me.resetButtons = resetButtons;
    }
});