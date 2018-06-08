package ru.nsc.interval.knime;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "SupergradientSolvty" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Vladimir Misyura
 */
public class SupergradientSolvtyNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring SupergradientSolvty node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected SupergradientSolvtyNodeDialog() {
        super();
        
        addDialogComponent(
        		new DialogComponentStringSelection(
        				new SettingsModelString(
        						SupergradientSolvtyNodeModel.CFGKEY_FUNC,
        						SupergradientSolvtyNodeModel.DEFAULT_FUNC
        						),
        				"Functional:",
        				new String[] {"Tol", "Uni", "Uss", "Uns"}
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelInteger(
        						SupergradientSolvtyNodeModel.CFGKEY_MAXITN,
        						SupergradientSolvtyNodeModel.DEFAULT_MAXITN
        						),
        				"maxitn ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelInteger(
        						SupergradientSolvtyNodeModel.CFGKEY_NSIMS,
        						SupergradientSolvtyNodeModel.DEFAULT_NSIMS
        						),
        				"nsims ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_EPSF,
        						SupergradientSolvtyNodeModel.DEFAULT_EPSF
        						),
        				"epsf ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_EPSX,
        						SupergradientSolvtyNodeModel.DEFAULT_EPSX
        						),
        				"epsx ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_EPSG,
        						SupergradientSolvtyNodeModel.DEFAULT_EPSG
        						),
        				"epsg ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_ALPHA,
        						SupergradientSolvtyNodeModel.DEFAULT_ALPHA
        						),
        				"alpha ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_HS,
        						SupergradientSolvtyNodeModel.DEFAULT_HS
        						),
        				"hs ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelInteger(
        						SupergradientSolvtyNodeModel.CFGKEY_NH,
        						SupergradientSolvtyNodeModel.DEFAULT_NH
        						),
        				"nh ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_Q1,
        						SupergradientSolvtyNodeModel.DEFAULT_Q1
        						),
        				"q1 ="
        				)
        		);
        addDialogComponent(
        		new DialogComponentNumberEdit(
        				new SettingsModelDouble(
        						SupergradientSolvtyNodeModel.CFGKEY_Q2,
        						SupergradientSolvtyNodeModel.DEFAULT_Q2
        						),
        				"q2 ="
        				)
        		);
        
    }
}

