Ext.define("TR.view.rests.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    title: 'ნაშთები',
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var panel = Ext.create('Ext.panel.Panel',{
            defaultType : 'textfield',
            id: 'rests-panel',
            title : 'მიმდინარე ნაშთები',
            buttonAlign: 'left',
            buttons: [{
                text : 'განახლება',
                handler : load
            }]
        });
        
        me.items = [ panel ];
        
        me.callParent(arguments);
        
        me.addListener({
            activate: load
        });
        
        function load() {
            panel.mask();
            myRequest({
                url : 'rest/transaction/rests/currencies',
                method : 'GET',
                callback : function(data){
                    panel.removeAll();
                    Ext.Array.forEach(data, function(item){
                        panel.add({
                            fieldLabel : item.resourceName,
                            labelAlign : 'right',
                            labelWidth : 150,
                            value: item.transactionRest,
                            readOnly: true
                        });
                    });
                    panel.unmask();
                }
            });
        }
    }
});