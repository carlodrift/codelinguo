package fr.unilim.codelinguo.intellij_plugin;

import com.intellij.AppTopics;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import fr.unilim.codelinguo.common.model.Word;
import fr.unilim.codelinguo.common.model.reader.FileReader;
import fr.unilim.codelinguo.common.service.WordAnalyticsService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;
import java.util.Map;

public class Panel implements ToolWindowFactory {

    private DefaultTableModel model;
    private Project currentProject;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.currentProject = project;
        JPanel myToolWindowContent = new JPanel();
        myToolWindowContent.setLayout(new BoxLayout(myToolWindowContent, BoxLayout.Y_AXIS));
        myToolWindowContent.setBorder(JBUI.Borders.empty(15));

        String[] columnNames = {"Word", "Occurrence"};
        this.model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JBTable(this.model);

        this.styleTable(table);

        JScrollPane scrollPane = new JBScrollPane(table);
        myToolWindowContent.add(scrollPane);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);

        this.updateTableModel();

        project.getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerListener() {
            @Override
            public void beforeDocumentSaving(@NotNull Document document) {
                SwingUtilities.invokeLater(Panel.this::updateTableModel);
            }
        });

        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                SwingUtilities.invokeLater(Panel.this::updateTableModel);
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                SwingUtilities.invokeLater(Panel.this::updateTableModel);
            }
        });
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(JBUI.scale(22));
        table.setBackground(JBColor.background());
        table.setForeground(JBColor.foreground());
        table.setSelectionBackground(JBColor.lightGray);
        table.setSelectionForeground(JBColor.darkGray);
        table.setGridColor(JBColor.gray);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setDefaultRenderer(headerRenderer);
        tableHeader.setBackground(JBColor.background());
        tableHeader.setForeground(JBColor.foreground());
        tableHeader.setBorder(JBUI.Borders.empty());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(1).setPreferredWidth(JBUI.scale(100));
        table.getColumnModel().getColumn(0).setPreferredWidth(JBUI.scale(200));
    }

    private void updateTableModel() {
        if (this.currentProject == null) {
            return;
        }

        List<Word> analysisWords = new FileReader().readOne(this.getCurrentFilePath(this.currentProject));
        WordAnalyticsService analytics = new WordAnalyticsService();
        Map<Word, Integer> wordRank = analytics.wordRank(analysisWords);

        this.model.setRowCount(0);

        for (Map.Entry<Word, Integer> entry : wordRank.entrySet()) {
            Word word = entry.getKey();
            Integer occurrence = entry.getValue();
            Object[] row = {word.getToken(), occurrence};
            this.model.addRow(row);
        }
    }

    public String getCurrentFilePath(Project project) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (editor != null) {
            Document document = editor.getDocument();
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
            if (virtualFile != null) {
                return virtualFile.getPath();
            }
        }
        return null;
    }
}
