package org.bbop.apollo.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.bbop.apollo.gwt.client.dto.AnnotationInfo;
import org.bbop.apollo.gwt.client.resources.TableResources;
import org.bbop.apollo.gwt.shared.go.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.ConfirmCallback;

import java.util.List;

/**
 * Created by ndunn on 1/9/15.
 */
public class GoPanel extends Composite {


    interface GoPanelUiBinder extends UiBinder<Widget, GoPanel> {
    }

//    private GoAnnotation internalGoAnnotation;
    private static GoPanelUiBinder ourUiBinder = GWT.create(GoPanelUiBinder.class);

//    @UiField
//    Container goEditContainer;
    DataGrid.Resources tablecss = GWT.create(TableResources.TableCss.class);
    @UiField(provided = true)
    DataGrid<GoAnnotation> dataGrid = new DataGrid<>(200, tablecss);
    @UiField
    TextBox referenceField;
    @UiField
    TextBox goTermField;
    @UiField
    TextBox evidenceCodeField;
    @UiField
    TextBox withField;
    @UiField
    Button deleteGoButton;
    @UiField
    Button newGoButton;
    @UiField
    Modal editGoModal;
    @UiField
    Button saveNewGoAnnotation;
    @UiField
    Button cancelNewGoAnnotation;
    //    @UiField
//    HTML notePanel;
    private static ListDataProvider<GoAnnotation> dataProvider = new ListDataProvider<>();
    private static List<GoAnnotation> annotationInfoList = dataProvider.getList();
    private SingleSelectionModel<GoAnnotation> selectionModel = new SingleSelectionModel<>();

    public GoPanel() {
        dataGrid.setWidth("100%");
        initializeTable();
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                if (selectionModel.getSelectedSet().isEmpty()) {
                    goTermField.setText("");
                    withField.setText("");
                    referenceField.setText("");
                    evidenceCodeField.setText("");
//                    goEditContainer.setVisible(false);
                } else {
                    goTermField.setText(selectionModel.getSelectedObject().getGoTerm().getName());
                    withField.setText(selectionModel.getSelectedObject().getWithOrFromString());
                    referenceField.setText(selectionModel.getSelectedObject().getReferenceString());

                    evidenceCodeField.setText(selectionModel.getSelectedObject().getEvidenceCode().name());
//                    goEditContainer.setVisible(true);
                }
            }
        });


        initWidget(ourUiBinder.createAndBindUi(this));

        addFakeData(50);
        redraw();

    }

    public void redraw() {
        dataGrid.redraw();
    }

    private void addFakeData(int amountOfData) {
        annotationInfoList.clear();
        for(int i = 0 ; i < amountOfData ; i++){
            GoAnnotation goAnnotation = new GoAnnotation();
            goAnnotation.setGoTerm(new GoTerm("GO:12312","green blood"));
            goAnnotation.setEvidenceCode(EvidenceCode.IEA);
            goAnnotation.addQualifier(Qualifier.NOT);
            goAnnotation.addWithOrFrom(new WithOrFrom("UniProtKB-KW:KW-0067"));
            goAnnotation.addWithOrFrom(new WithOrFrom("InterPro:IPR000719"));
            goAnnotation.addReference(new Reference("PMID:21873635"));
            goAnnotation.addReference(new Reference("GO_REF:0000002"));
            annotationInfoList.add(goAnnotation);
        }
        GWT.log("fake data size: "+annotationInfoList.size());
    }

    @UiHandler("newGoButton")
    public void newGoAnnotation(ClickEvent e) {
        editGoModal.show();
    }

    @UiHandler("saveNewGoAnnotation")
    public void saveNewGoAnnotationButton(ClickEvent e) {
        editGoModal.hide();
    }

    @UiHandler("cancelNewGoAnnotation")
    public void cancelNewGoAnnotationButton(ClickEvent e) {
        editGoModal.hide();
    }

    @UiHandler("deleteGoButton")
    public void deleteGoAnnotation(ClickEvent e) {
        GoAnnotation goAnnotation = selectionModel.getSelectedObject();
        Bootbox.confirm("Remove GO Annotation: " + goAnnotation.getGoTerm().getName(), new ConfirmCallback() {
            @Override
            public void callback(boolean result) {
                Window.alert("removed: "+result);
            }
        });
    }



    private void initializeTable() {
        // TODO: probably want a link here
        TextColumn<GoAnnotation> goTermColumn = new TextColumn<GoAnnotation>() {
            @Override
            public String getValue(GoAnnotation annotationInfo) {
                return annotationInfo.getGoTerm().getName();
            }
        };
        goTermColumn.setSortable(true);

        TextColumn<GoAnnotation> withColumn = new TextColumn<GoAnnotation>() {
            @Override
            public String getValue(GoAnnotation annotationInfo) {
                return annotationInfo.getWithOrFromString();
            }
        };
        withColumn.setSortable(true);

        TextColumn<GoAnnotation> referenceColumn = new TextColumn<GoAnnotation>() {
            @Override
            public String getValue(GoAnnotation annotationInfo) {
                return annotationInfo.getReferenceString();
            }
        };
        referenceColumn.setSortable(true);

        TextColumn<GoAnnotation> evidenceColumn = new TextColumn<GoAnnotation>() {
            @Override
            public String getValue(GoAnnotation annotationInfo) {
                return annotationInfo.getEvidenceCode().name();
            }
        };
        evidenceColumn.setSortable(true);


        dataGrid.addColumn(goTermColumn, "Name");
        dataGrid.addColumn(evidenceColumn, "Evidence");
        dataGrid.addColumn(withColumn, "Based On");
        dataGrid.addColumn(referenceColumn, "Reference");

        dataGrid.setColumnWidth(0, "70px");
        dataGrid.setColumnWidth(1, "30px");
        dataGrid.setColumnWidth(2, "90px");
        dataGrid.setColumnWidth(3, "90px");

//        ColumnSortEvent.ListHandler<GoAnnotation> sortHandler = new ColumnSortEvent.ListHandler<GoAnnotation>(annotationInfoList);
//        dataGrid.addColumnSortHandler(sortHandler);

//        sortHandler.setComparator(goTermColumn, new Comparator<GoAnnotation>() {
//            @Override
//            public int compare(GoAnnotation o1, GoAnnotation o2) {
//                return o1.getType().compareTo(o2.getType());
//            }
//        });
//
//        sortHandler.setComparator(withColumn, new Comparator<GoAnnotation>() {
//            @Override
//            public int compare(GoAnnotation o1, GoAnnotation o2) {
//                return o1.getMin() - o2.getMin();
//            }
//        });
//
//        sortHandler.setComparator(referenceColumn, new Comparator<GoAnnotation>() {
//            @Override
//            public int compare(GoAnnotation o1, GoAnnotation o2) {
//                return o1.getMax() - o2.getMax();
//            }
//        });
//
//        sortHandler.setComparator(lengthColumn, new Comparator<GoAnnotation>() {
//            @Override
//            public int compare(GoAnnotation o1, GoAnnotation o2) {
//                return o1.getLength() - o2.getLength();
//            }
//        });
    }

    public void updateData() {
        updateData(null);
    }

    public void updateData(AnnotationInfo selectedAnnotationInfo) {
        addFakeData(50);
//        if(selectedAnnotationInfo==null){
//            dataProvider.setList(new ArrayList<GoAnnotation>());
//        }
//        else{
//            dataProvider.setList(selectedAnnotationInfo.getGoAnnotations());
//        }
    }

}
