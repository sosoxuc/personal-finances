Ext.onReady(function() {
    if (Ext.Date) {
        Ext.Date.defaultDateFormat = "d.m.Y";
        Ext.Date.monthNames = [ "იანვარი", "თებერვალი", "მარტი", "აპრილი",
                "მაისი", "ივნისი", "ივლისი", "აგვისტო", "სექტემბერი",
                "ოქტომბერი", "ნოემბერი", "დეკემბერი" ];
        Ext.Date.getShortMonthName = function(month) {
            return Ext.Date.monthNames[month].substring(0, 3);
        };
        Ext.Date.monthNumbers = {
            იან : 0,
            თებ : 1,
            მარ : 2,
            აპრ : 3,
            მაი : 4,
            ივნ : 5,
            ივლ : 6,
            აგვ : 7,
            სექ : 8,
            ოტქ : 9,
            ნოე : 10,
            დეკ : 11
        };
        Ext.Date.getMonthNumber = function(name) {
            return Ext.Date.monthNumbers[name.substring(0, 1).toUpperCase()
                    + name.substring(1, 3).toLowerCase()];
        };
        Ext.Date.dayNames = [ "კვირა", "ორშაბათი", "სამშაბათი", "ოთხშაბათი",
                "ხუთშაბათი", "პარასკევი", "შაბათი" ];
        Ext.Date.getShortDayName = function(day) {
            return Ext.Date.dayNames[day].substring(0, 3);
        };
        Ext.Date.parseCodes.S.s = "(?:st|nd|rd|th)";
    }
    if (Ext.util && Ext.util.Format) {
        Ext.apply(Ext.util.Format, {
            thousandSeparator : ',',
            decimalSeparator : '.',
            currencySign : 'ლარი',
            dateFormat : 'd.m.Y'
        });
    }
});
Ext.define("Ext.locale.ka_GE.view.View", {
    override : "Ext.view.View",
    emptyText : ""
});
Ext.define("Ext.locale.ka_GE.grid.plugin.DragDrop", {
    override : "Ext.grid.plugin.DragDrop",
    dragText : "{0} მონიშნული სტრიქონი {1}"
});
Ext.define("Ext.locale.ka_GE.view.AbstractView", {
    override : "Ext.view.AbstractView",
    loadingText : "იტვირთება..."
});
Ext.define("Ext.locale.ka_GE.picker.Date", {
    override : "Ext.picker.Date",
    todayText : "დღეს",
    minText : "ეს თარიღი არის მინიმალურ თარიღზე ნაკლები",
    maxText : "ეს თარიღი არის მაქსიმალურ თარიღზე მეტი",
    disabledDaysText : "",
    disabledDatesText : "",
    nextText : 'შემდეგი თვე (Control+Right)',
    prevText : 'წინა თვე (Control+Left)',
    monthYearText : 'აირჩიეთ თვე (Control+Up/Down წლების გადასართველად)',
    todayTip : "{0} (Spacebar)",
    format : "d.m.Y",
    startDay : 1
});
Ext.define("Ext.locale.ka_GE.picker.Month", {
    override : "Ext.picker.Month",
    okText : "&#160;დიახ&#160;",
    cancelText : "გაუქმება"
});
Ext.define("Ext.locale.ka_GE.toolbar.Paging", {
    override : "Ext.PagingToolbar",
    beforePageText : "გვერდი",
    afterPageText : "{0}-დან",
    firstText : "პირველი გვერდი",
    prevText : "წინა გვერდი",
    nextText : "შემდეგი გვერდი",
    lastText : "ბოლო გვერდი",
    refreshText : "განახლება",
    displayMsg : "{0} - {1} {2}-დან",
    emptyMsg : 'მონაცემები არ არის'
});
Ext.define("Ext.locale.ka_GE.form.Basic", {
    override : "Ext.form.Basic",
    waitTitle : "გთხოვთ დაიცადოთ..."
});
Ext.define("Ext.locale.ka_GE.form.field.Base", {
    override : "Ext.form.field.Base",
    invalidText : "არასწორი მნიშვნელობა"
});
Ext.define("Ext.locale.ka_GE.form.field.Text", {
    override : "Ext.form.field.Text",
    minLengthText : "ველის მინიმალური სიგრძე არის{0}",
    maxLengthText : "ველის მაქსიმალური სიგრძე არის{0}",
    blankText : "ველის შევსება აუცილებელია",
    regexText : "",
    emptyText : null
});
Ext.define("Ext.locale.ka_GE.form.field.Number", {
    override : "Ext.form.field.Number",
    decimalPrecision : 2,
    minText : "ველის მინიმალური მნიშვნელობა არის{0}",
    maxText : "ველის მაქსიმალური მნიშვნელობა არის{0}",
    nanText : "{0} არ არის რიცხვი"
});
Ext.define("Ext.locale.ka_GE.form.field.Date", {
    override : "Ext.form.field.Date",
    disabledDaysText : "გაუქმებული",
    disabledDatesText : "გაუქმებული",
    minText : "თარიღი უნდა იყოს {0}-ზე მეტი",
    maxText : "თარიღი უნდა იყოს {0}-ზე ნაკლები",
    invalidText : "{0} თარიღი არ არის სწორი,უნდა იყოს შემდეგი ფორმატის {1}",
    format : "d.m.Y",
    altFormats : "d.m.Y|d.m.Y|d-m-y|d-m-Y|d/m|d-m|dm|dmy|dmY|d|Y-m-d"
});
Ext.define("Ext.locale.ka_GE.form.field.ComboBox", {
    override : "Ext.form.field.ComboBox",
    valueNotFoundText : undefined
}, function() {
    Ext.apply(Ext.form.field.ComboBox.prototype.defaultListConfig, {
        loadingText : "იტვირთება..."
    });
});
Ext.define("Ext.locale.ka_GE.form.field.VTypes", {
    override : "Ext.form.field.VTypes",
    emailText : 'ელ. ფოსტა უნდა იყოს შემდეგი ფორმატის "user@example.com"',
    urlText : 'ველში უნდა იყოს URL ფორმატი: "http:/' + '/www.example.com"',
    alphaText : 'ეს ველი უნდა შეიცავდეს მხოლოდ ასოებს და _ სიმბოლოს',
    alphanumText : 'ეს ველი უნდა შეიცავდეს მხოლოდ ასოებს,ციფრებს და _ სიმბოლოს'
});
Ext.define("Ext.locale.ka_GE.form.field.HtmlEditor", {
    override : "Ext.form.field.HtmlEditor",
    createLinkText : 'გთხოვთ მიუთითოთ URL მისამართი ბმულისთვის:'
}, function() {
    Ext.apply(Ext.form.field.HtmlEditor.prototype, {
        buttonTips : {
            bold : {
                title : 'Bold (Ctrl+B)',
                text : 'მონიშნული ტექსტის გაუქმება.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            italic : {
                title : 'Italic (Ctrl+I)',
                text : 'მონიშნული ტექსტის დახრა.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            underline : {
                title : 'Underline (Ctrl+U)',
                text : 'მონიშნული ტექსტის ხაზგასმა.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            increasefontsize : {
                title : 'Grow Text',
                text : 'ტექსტის ზომის გაზრდა.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            decreasefontsize : {
                title : 'Shrink Text',
                text : 'ტექსტის ზომის შემცირება.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            backcolor : {
                title : 'Text Highlight Colour',
                text : 'მონიშნული ტექსტის ფონის ფერის შეცვლა.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            forecolor : {
                title : 'Font Colour',
                text : 'მონიშნული ტექსტის ფერის შეცვლა.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            justifyleft : {
                title : 'Align Text Left',
                text : 'ტექსტის გასწორება მარცხნივ.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            justifycenter : {
                title : 'Centre Text',
                text : 'ტექსტის გასწორება ცენტრში.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            justifyright : {
                title : 'Align Text Right',
                text : 'ტექსტის გასწორება მარჯვნივ.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            insertunorderedlist : {
                title : 'Bullet List',
                text : 'დაუნომრავი სია.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            insertorderedlist : {
                title : 'Numbered List',
                text : 'დანომრილი სია.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            createlink : {
                title : 'Hyperlink',
                text : 'მონიშნულ ტექსტზე ბმულის დადება.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            },
            sourceedit : {
                title : 'Source Edit',
                text : 'კოდის რედაქტირების რეჟიმზე გადართვა.',
                cls : Ext.baseCSSPrefix + 'html-editor-tip'
            }
        }
    });
});
Ext.define("Ext.locale.ka_GE.grid.header.Container", {
    override : "Ext.grid.header.Container",
    sortAscText : "დალაგება ზემოდან ქვემოთ",
    sortDescText : "დალაგება ქვემოდან ზემოთ",
    columnsText : "სვეტები"
});
Ext.define("Ext.locale.ka_GE.grid.DateColumn", {
    override : "Ext.grid.DateColumn",
    format : 'd.m.Y'
});
Ext.define("Ext.locale.ka_GE.grid.GroupingFeature", {
    override : "Ext.grid.feature.Grouping",
    emptyGroupText : '(None)',
    groupByText : 'დაჯგუფება ამ ველით',
    showGroupsText : 'ჯგუფებში გამოჩენა'
});
Ext.define("Ext.locale.ka_GE.grid.PropertyColumnModel", {
    override : "Ext.grid.PropertyColumnModel",
    nameText : "სახელი",
    valueText : "მნიშვნელობა",
    dateFormat : "d.m.Y",
    trueText : "true",
    falseText : "false"
});
Ext
        .define(
                "Ext.locale.ka_GE.form.field.Time",
                {
                    override : "Ext.form.field.Time",
                    minText : "დრო უნდა იყოს მეტი ან ტოლი {0}-ის",
                    maxText : "დრო უნდა იყოს ნაკლები ან ტოლი {0}-ის",
                    invalidText : "{0} არასწორი დრო",
                    format : "g:i A",
                    altFormats : "g:ia|g:iA|g:i a|g:i A|h:i|g:i|H:i|ga|ha|gA|h a|g a|g A|gi|hi|gia|hia|g|H"
                });
Ext.define("Ext.locale.ka_GE.form.CheckboxGroup", {
    override : "Ext.form.CheckboxGroup",
    blankText : "ერთ-ერთი მაინც უნდა იყოს არჩეული ამ ჯგუფში"
});
Ext.define("Ext.locale.ka_GE.form.RadioGroup", {
    override : "Ext.form.RadioGroup",
    blankText : "ერთ-ერთი მაინც უნდა იყოს არჩეული ამ ჯგუფში"
});
Ext.define("Ext.locale.ka_GE.window.MessageBox", {
    override : "Ext.window.MessageBox",
    buttonText : {
        ok : "დიახ",
        cancel : "გაუქმება",
        yes : "დიახ",
        no : "არა"
    }
});
Ext.define("Ext.locale.ka_GE.Component", {
    override : "Ext.Component"
});
