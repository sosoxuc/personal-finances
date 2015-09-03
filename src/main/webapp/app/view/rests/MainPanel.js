Ext.define("TR.view.rests.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    title: LANG.RESTS,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var rests = Ext.create("TR.view.rests.RestsPanel",{
            load: load
        });
        
        var outdateds = Ext.create("TR.view.rests.OutdatedsPanel",{
        });
        
        me.items = [ rests, outdateds ];
        
        me.callParent(arguments);
        
        me.addListener({
            activate: load
        });
        
        function load() {
            rests.mask(LANG.LOADING);
            myRequest({
                url : 'rest/transaction/rests/currencies',
                method : 'GET',
                callback : function(data){
                    rests.loadData(data);
                }
            });
        }
    }
});