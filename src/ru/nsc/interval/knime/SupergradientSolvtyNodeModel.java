package ru.nsc.interval.knime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import net.java.jinterval.interval.set.SetInterval;
import net.java.jinterval.interval.set.SetIntervalContext;
import net.java.jinterval.interval.set.SetIntervalContexts;
import solvty.ICalculatorInjector;
import solvty.SolvtyValue;
import solvty.tol.TolSolvtyInjector;
import solvty.uni.UniSolvtyInjector;
import solvty.uns.UnsSolvtyInjector;
import solvty.uss.UssSolvtyInjector;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of SupergradientSolvty.
 * 
 *
 * @author SibIntVal
 */
public class SupergradientSolvtyNodeModel extends NodeModel {
    final SetIntervalContext ic = SetIntervalContexts.getAccur64();
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(SupergradientSolvtyNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";

    /** initial default count value. */
    static final int DEFAULT_COUNT = 100;

    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".
	static final String CFGKEY_FUNC = "Functional";
	static final String DEFAULT_FUNC = "Tol";
	private final SettingsModelString funcValue =
			new SettingsModelString(
			SupergradientSolvtyNodeModel.CFGKEY_FUNC,
			SupergradientSolvtyNodeModel.DEFAULT_FUNC
			);
	static final String CFGKEY_MAXITN = "maxitn";
	static final int DEFAULT_MAXITN = 2000;
	private final SettingsModelInteger maxitnValue =
			new SettingsModelInteger(
			SupergradientSolvtyNodeModel.CFGKEY_MAXITN,
			SupergradientSolvtyNodeModel.DEFAULT_MAXITN
			);
	static final String CFGKEY_NSIMS = "nsims";
	static final int DEFAULT_NSIMS = 30;
	private final SettingsModelInteger nsimsValue =
			new SettingsModelInteger(
			SupergradientSolvtyNodeModel.CFGKEY_NSIMS,
			SupergradientSolvtyNodeModel.DEFAULT_NSIMS
			);
	static final String CFGKEY_EPSF = "epsf";
	static final double DEFAULT_EPSF = 1.e-6;
	private final SettingsModelDouble epsfValue =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_EPSF,
			SupergradientSolvtyNodeModel.DEFAULT_EPSF
			);
	static final String CFGKEY_EPSX = "epsx";
	static final double DEFAULT_EPSX = 1.e-6;
	private final SettingsModelDouble epsxValue =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_EPSX,
			SupergradientSolvtyNodeModel.DEFAULT_EPSX
			);	
	static final String CFGKEY_EPSG = "epsg";
	static final double DEFAULT_EPSG = 1.e-6;
	private final SettingsModelDouble epsgValue =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_EPSG,
			SupergradientSolvtyNodeModel.DEFAULT_EPSG
			);
	static final String CFGKEY_ALPHA = "alpha";
	static final double DEFAULT_ALPHA = 2.3;
	private final SettingsModelDouble alphaValue =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_ALPHA,
			SupergradientSolvtyNodeModel.DEFAULT_ALPHA
			);
	static final String CFGKEY_HS = "hs";
	static final double DEFAULT_HS = 1.0;
	private final SettingsModelDouble hsValue =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_HS,
			SupergradientSolvtyNodeModel.DEFAULT_HS
			);
	static final String CFGKEY_NH = "nh";
	static final int DEFAULT_NH = 3;
	private final SettingsModelInteger nhValue =
			new SettingsModelInteger(
			SupergradientSolvtyNodeModel.CFGKEY_NH,
			SupergradientSolvtyNodeModel.DEFAULT_NH
			);
	static final String CFGKEY_Q1 = "q1";
	static final double DEFAULT_Q1 = 1.0;
	private final SettingsModelDouble q1Value =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_Q1,
			SupergradientSolvtyNodeModel.DEFAULT_Q1
			);
	static final String CFGKEY_Q2 = "q2";
	static final double DEFAULT_Q2 = 1.1;
	private final SettingsModelDouble q2Value =
			new SettingsModelDouble(
			SupergradientSolvtyNodeModel.CFGKEY_Q2,
			SupergradientSolvtyNodeModel.DEFAULT_Q2
			);
	
    /**
     * Constructor for the node model.
     */
    protected SupergradientSolvtyNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	BufferedDataTable inTable = inData[0];
    	int m = (int) inTable.size();
    	int n = inTable.getDataTableSpec().getNumColumns() / 2 - 1;
    	SetInterval[][] matrix_A = new SetInterval[m][n];
    	SetInterval[] vector_b = new SetInterval[m];
    	int nrows = 0;
    	int i = 0;
    	for (DataRow row: inTable) {
    		for (int j = 0; j < n; j++) {
    			matrix_A[i][j] = ic.numsToInterval(
    				((DoubleValue)row.getCell(j*2)).getDoubleValue(),
    				((DoubleValue)row.getCell(j*2+1)).getDoubleValue()
    			);
    		}
    		vector_b[i] = ic.numsToInterval(
    			((DoubleValue)row.getCell(n*2)).getDoubleValue(),
				((DoubleValue)row.getCell(n*2+1)).getDoubleValue()
    		);
    		i++;
    	}
    	
    	ICalculatorInjector func;
    	if (funcValue.getStringValue() == "Tol") {
    		func = new TolSolvtyInjector();
    	}
    	else if (funcValue.getStringValue() == "Uni") {
    		func = new UniSolvtyInjector();
    	}
    	else if (funcValue.getStringValue() == "Uss") {
    		func = new UssSolvtyInjector();
    	}
    	else {
    		func = new UnsSolvtyInjector();
    	}
        
        int N = 1<<n;
        String[] orthantsCodes = new String[N];
        int startCode = 0;
        for (int k = 0; k < N; k++) {
            orthantsCodes[k] = StringUtils.leftPad(Integer.toBinaryString(startCode), n, '0');
            startCode += 1;
        }

        int maxitn = maxitnValue.getIntValue();
        int nsims  = nsimsValue.getIntValue();
        double epsf = epsfValue.getDoubleValue();
        double epsx = epsxValue.getDoubleValue();
        double epsg = epsgValue.getDoubleValue();

        double alpha = alphaValue.getDoubleValue();
        double hs = hsValue.getDoubleValue();
        int nh = nhValue.getIntValue();
        double q1 = q1Value.getDoubleValue();
        double q2 = q2Value.getDoubleValue();
        
        SolvtyValue[] extremums = new SolvtyValue[N];
        double max = Double.NEGATIVE_INFINITY;
    	int kmax = 0;
        if (funcValue.getStringValue() == "Tol") {
        	extremums[kmax] = func.getAlgorithm().solve(matrix_A, vector_b, ic, Optional.empty(), maxitn, nsims, epsf, epsx, epsg, alpha, hs, nh, q1, q2);
        	max = extremums[kmax].getF();
        }
        else {
        	for (int k = 0; k < N; k++) {
            	extremums[k] = func.getAlgorithm().solve(matrix_A, vector_b, ic, Optional.of(orthantsCodes[k]),
            												maxitn, nsims, epsf, epsx, epsg, alpha, hs, nh, q1, q2);
            	if (max < extremums[k].getF()) {
            		max = extremums[k].getF();
                	kmax = k;
            	}
        	}
        }
    	
        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");

        
        // the data table spec of the single output table, 
        // the table will have three columns:
        DataColumnSpec[] outColSpecs = new DataColumnSpec[n+1];
        for (int j = 0; j < n; j++) {
           	outColSpecs[j] = 
           			new DataColumnSpecCreator("x_"+j, DoubleCell.TYPE).createSpec();
        }
        outColSpecs[n] = 
            	new DataColumnSpecCreator("FuncValue", DoubleCell.TYPE).createSpec();
        DataTableSpec outspec = new DataTableSpec(outColSpecs);

        ArrayList<Integer> maxs = new ArrayList<>();
        for (int k = 0; k < N; k++) {
           	if (extremums[k].getF() >= max - epsf) {
           		nrows++;
           		maxs.add(k);
			}
		}
        
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outspec);
        for (int l = 0; l < nrows; l++) {
            RowKey key = new RowKey("Row " + l);
            DataCell[] cells = new DataCell[n+1];
            for (int j = 0; j < n; j++) {
            	cells[j] = new DoubleCell(extremums[maxs.get(l)].getX()[j]);
            }
            cells[n] = new DoubleCell(extremums[maxs.get(l)].getF());
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
        }
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
        funcValue.saveSettingsTo(settings);
        maxitnValue.saveSettingsTo(settings);
        nsimsValue.saveSettingsTo(settings);
        epsfValue.saveSettingsTo(settings);
        epsxValue.saveSettingsTo(settings);
        epsgValue.saveSettingsTo(settings);
        alphaValue.saveSettingsTo(settings);
        hsValue.saveSettingsTo(settings);
        nhValue.saveSettingsTo(settings);
        q1Value.saveSettingsTo(settings);
        q2Value.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
        funcValue.loadSettingsFrom(settings);
        maxitnValue.loadSettingsFrom(settings);
        nsimsValue.loadSettingsFrom(settings);
        epsfValue.loadSettingsFrom(settings);
        epsxValue.loadSettingsFrom(settings);
        epsgValue.loadSettingsFrom(settings);
        alphaValue.loadSettingsFrom(settings);
        hsValue.loadSettingsFrom(settings);
        nhValue.loadSettingsFrom(settings);
        q1Value.loadSettingsFrom(settings);
        q2Value.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

        funcValue.validateSettings(settings);
        maxitnValue.validateSettings(settings);
        nsimsValue.validateSettings(settings);
        epsfValue.validateSettings(settings);
        epsxValue.validateSettings(settings);
        epsgValue.validateSettings(settings);
        alphaValue.validateSettings(settings);
        hsValue.validateSettings(settings);
        nhValue.validateSettings(settings);
        q1Value.validateSettings(settings);
        q2Value.validateSettings(settings);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

