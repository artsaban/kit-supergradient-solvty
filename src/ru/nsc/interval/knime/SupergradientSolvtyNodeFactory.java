package ru.nsc.interval.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "SupergradientSolvty" Node.
 * 
 *
 * @author Vladimir Misyura
 */
public class SupergradientSolvtyNodeFactory 
        extends NodeFactory<SupergradientSolvtyNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SupergradientSolvtyNodeModel createNodeModel() {
        return new SupergradientSolvtyNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<SupergradientSolvtyNodeModel> createNodeView(final int viewIndex,
            final SupergradientSolvtyNodeModel nodeModel) {
        return new SupergradientSolvtyNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new SupergradientSolvtyNodeDialog();
    }

}

