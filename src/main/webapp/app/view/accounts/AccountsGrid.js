Ext.define("TR.view.accounts.AccountsGrid", {
    extend : "Ext.grid.Panel",
    border : false,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        me.store = Ext.StoreManager.lookup('accountsStore') || Ext.create('TR.store.accounts.Store');
        
        me.tbar = [ {
            text : LANG.ADD,
            name : 'add',
            handler : add
        }, {
            text : LANG.EDIT,
            name : 'edit',
            disabled: true,
            handler : edit
        }, {
            text : LANG.REMOVE,
            name : 'remove',
            disabled: true,
            handler : remove
        } ];

        me.columns = [ {
            header : LANG.NAME,
            dataIndex : 'accountName',
            flex : 1
        }, {
            header : LANG.NUMBER,
            dataIndex : 'accountNumber',
            flex : 1
        } ];

        me.callParent(arguments);
        
        me.on('select', function(view, rec){
            me.down('button[name=edit]').enable();
            me.down('button[name=remove]').enable();
        });

        function add() {
            Ext.create('TR.view.accounts.AddWindow', {
                grid : me
            }).show();
        }
        
        function edit() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            var addWindow = Ext.create('TR.view.accounts.AddWindow', {
                grid : me,
                edit : true,
                data : sel[0].getData()
            }).show();
        }

        function remove() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;
            Ext.Msg.confirm(LANG.CONFIRM, LANG.CONFIRM_REMOVAL, function(ans) {
                if (ans === 'yes') {
                    var rec = sel[0];
                    myRequest({
                        url : 'rest/account/remove',
                        params : {
                            id : rec.get('id'),
                            version: rec.get('version')
                        },
                        callback : function(res) {
                            me.store.load();
                            me.getSelectionModel().deselectAll();
                            me.down('button[name=edit]').disable();
                            me.down('button[name=remove]').disable();
                        }
                    });
                }
            });
        }

    }
});