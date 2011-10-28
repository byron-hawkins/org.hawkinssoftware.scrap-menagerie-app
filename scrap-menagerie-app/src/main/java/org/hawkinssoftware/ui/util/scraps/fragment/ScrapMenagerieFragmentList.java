package org.hawkinssoftware.ui.util.scraps.fragment;

import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListSelection;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

@InvocationConstraint(domains = ModelListDomain.class)
@DomainRole.Join(membership = ModelListDomain.class)
public class ScrapMenagerieFragmentList extends ScrollPaneComposite<ScrapMenagerieListViewport>
{
	public static class ScrollPaneAssembly extends ScrollPane.Assembly<ScrapMenagerieListViewport, ScrapMenagerieFragmentList>
	{
		public ScrollPaneAssembly()
		{
			super(ScrapMenagerieFragmentList.class, new ScrapMenagerieListViewport.Assembly());
		}
	}

	private final ListDataModel model = new ListDataModel();
	private final ScrapMenagerieFragmentStampFactory stampFactory = new ScrapMenagerieFragmentStampFactory();
	
	@InvocationConstraint
	public ScrapMenagerieFragmentList(ScrollPane component)
	{
		super(component);
	}

	public ListDataModel getModel()
	{
		return model;
	} 

	@SuppressWarnings("unchecked")
	@Override
	public <ServiceType> ServiceType getService(Class<ServiceType> serviceType)
	{
		if (serviceType.isAssignableFrom(ListDataModel.class))
		{
			return (ServiceType) model;
		}
		if (serviceType.isAssignableFrom(ScrapMenagerieListViewport.class))
		{
			return (ServiceType) getViewport();
		}
		if (serviceType.isAssignableFrom(ScrapMenagerieFragmentStampFactory.class))
		{
			return (ServiceType) stampFactory;
		}
		if (serviceType.isAssignableFrom(ListModelPainter.class))
		{
			return (ServiceType) getViewport().getCellPainter();
		}
		if (serviceType.isAssignableFrom(ScrapMenagerieListSelection.class))
		{
			return (ServiceType) getViewport().selection;
		}
		return super.getService(serviceType);
	}
}
