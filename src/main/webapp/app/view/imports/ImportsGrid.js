Ext.define("TR.view.imports.ImportsGrid", {
    extend : "Ext.grid.Panel",
    border : false,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        //me.store = Ext.StoreManager.lookup('importsStore') || Ext.create('TR.store.imports.Store');
        
        me.tbar = [ {
            text : 'დამატება',
            name : 'add',
            handler : add
        }, {
            text : 'რედაქტირება',
            name : 'edit',
            handler : edit
        }, {
            text : 'წაშლა',
            name : 'remove',
            handler : remove
        } ];

        me.columns = [ {
            header : 'დასახელება',
            dataIndex : 'projectName',
            flex : 1
        } ];

        me.callParent(arguments);

        function add() {
            Ext.create('TR.view.imports.AddWindow', {
                grid : me
            });
        }
        
        function edit() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            var addWindow = Ext.create('TR.view.imports.AddWindow', {
                grid : me,
                edit : true,
                data : sel[0].getData()
            });
        }

        function remove() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;
            Ext.Msg.confirm('გაფრთხილება', 'დაადასტურეთ წაშლა!', function(ans) {
                if (ans === 'yes') {
                    var rec = sel[0];
                    myRequest({
                        url : 'rest/import/remove',
                        params : {
                            id : rec.get('id'),
                            version: rec.get('version')
                        },
                        callback : function(res) {
                            me.store.load();
                            me.getSelectionModel().deselectAll();
                        }
                    });
                }
            });
        }

    }
});