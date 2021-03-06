Ext.define('TR.view.LoginPanel', {
    extend : 'Ext.panel.Panel',
    layout : 'fit',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        var form = Ext.create('Ext.form.Panel', {
            border : false,
            bodyPadding : 5,
            submitFn : login,
            fieldDefaults : {
                labelWidth : 130,
                labelAlign : 'right',
                anchor : '100%',
                allowBlank : false,
                blankText: LANG.REQUIRDE_FIELD,
                msgTarget: 'side'
            },
            items : [ {
                xtype : 'textfield',
                fieldLabel : LANG.USERNAME,
                name : 'username',
                isEng : true,
                value : localStorage.turnicetUserName || '',
                autoFocus : !localStorage.turnicetUserName
            }, {
                xtype : 'textfield',
                fieldLabel : LANG.PASSWORD,
                isEng : true,
                inputType : 'password',
                name : 'password',
                autoFocus : localStorage.turnicetUserName
            }]
        });

        Ext.create('Ext.window.Window', {
            modal : true,
            autoShow : true,
            width : 350,
            title : LANG.AUTH,
            closable : false,
            draggable : false,
            layout : 'fit',
            items : [ form ],
            buttons : [ {
                text : LANG.LOGIN,
                handler : login
            } ]
        });

        me.callParent(arguments);

        function openRegisterWindow() {
            Ext.create('TR.view.RegisterWindow');
        }

        function login() {
            if (!form.getForm().isValid())
                return;
            var values = form.getForm().getValues();
            myRequest({
                url : 'rest/security/signin',
                params : values,
                callback : function(response) {

                    var msg = '';
                    switch (response.authResult) {
                    case "SUCCESSFUL":
                        localStorage.turnicetUserName = values.username;
//                        sessionStorage.turnicetEmployeeId = response.employeeId;
//                        sessionStorage.turnicetUserRole = response.userRole;
                        //localStorage.turnicetFullName = response.fullName;
                        location.href = ".";
                        break;
                    case "EMPTY_USER_PASSWORD":
                        msg = 'მიუთითეთ პაროლი';
                        break;
                    case "BAD_USER":
                        msg = 'ასეთი მომხმარებელი არ არსებობს! გთხოვთ გაიაროთ რეგისტრაცია!';
                        break;
                    case "BAD_PASSWORD":
                        msg = 'არასწორი პაროლი';
                        break;
                    }
                    if(msg){
                        Ext.Msg.alert('შეცდომა', msg);
                    }
                }
            });
        }
    }
});