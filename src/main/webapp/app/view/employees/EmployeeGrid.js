Ext.define("TR.view.employees.EmployeeGrid", {
    extend : "Ext.grid.Panel",
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.store = Ext.StoreManager.lookup('employeeStore') || Ext.create('TR.store.employees.EmployeeStore');

        me.tbar = [ {
            text : LANG.ADD,
            name : 'add',
            handler : add
        }, {
            text : LANG.EDIT,
            name : 'edit',
            handler : edit
        }, {
            text : LANG.DISABLE,
            name : 'disable',
            handler : disable
        }, {
            text : LANG.ENABLE,
            name : 'enable',
            handler : enable
        }, {
            text : LANG.REMOVE,
            name : 'remove',
            handler : remove
        } ];

        me.columns = [ {
            header : LANG.PERSONAL_NUMBER,
            dataIndex : 'personalNo',
            flex : 1
        }, {
            header : LANG.LASTNAME,
            dataIndex : 'lastName',
            flex : 1
        }, {
            header : LANG.FIRSTNAME,
            dataIndex : 'firstName',
            flex : 1
        }, {
            header : LANG.BIRTHDATE,
            dataIndex : 'birthDate',
            flex : 1
        }, {
            header : LANG.WORKPLACE,
            dataIndex : 'workplaceName',
            flex : 1
        }, {
            header : LANG.POSITION,
            dataIndex : 'positionName',
            flex : 1
        }, {
            header : LANG.PHONE,
            dataIndex : 'phone',
            flex : 1
        }, {
            header : LANG.EMAIL,
            dataIndex : 'email',
            flex : 1
        }, {
            header : LANG.STATUS,
            dataIndex : 'stateName',
            flex : 1
        }, {
            header : LANG.USERNAME,
            dataIndex : 'userName',
            flex : 1
        }];

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

            Ext.create('TR.view.employees.AddWindow', {
                grid : me,
                edit : true,
                employeeId : sel[0].get('id'),
                searchForm : me.searchForm,
                data: sel[0].getData()
            });
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
                                url : 'rest/hr/employee/remove',
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

            me.btns.edit.disable();
            me.btns.disable.disable();
            me.btns.enable.disable();
            me.btns.remove.disable();
        }

        function changeButtons(view, rec, ind) {
            if (!me.btns)
                resetButtons();

            var state = rec.get('stateId');
            me.btns.edit.enable();
            if (!state || state == 1) {
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