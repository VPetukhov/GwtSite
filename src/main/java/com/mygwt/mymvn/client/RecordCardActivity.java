package com.mygwt.mymvn.client;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.activity.shared.AbstractActivity;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.mygwt.mymvn.client.events.DeleteEvent;
import com.mygwt.mymvn.client.places.RecordCardPlace;
import com.mygwt.mymvn.client.places.RecordEditPlace;
import com.mygwt.mymvn.client.rpc.DeleteAction;
import com.mygwt.mymvn.client.rpc.DeleteResult;
import com.mygwt.mymvn.client.rpc.GetByIdAction;
import com.mygwt.mymvn.client.rpc.GetByIdResult;
import com.mygwt.mymvn.client.widgets.RecordCardWidget;
import com.mygwt.mymvn.shared.PhoneRecord;

public class RecordCardActivity extends AbstractActivity implements
		RecordCardWidget.RecordCardPresenter
{
	private final DispatchAsync dispatcher;
	private final RecordCardWidget display;
	private final ClientFactory clientFactory;
	private final long recordId;

	public RecordCardActivity(RecordCardPlace place, ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
		dispatcher = clientFactory.getDispatcher();
		display = clientFactory.getRecordCardWidget();
		recordId = place.getRecordId();
	}

	@Override
	public void start(final AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus)
	{
		Window.alert(Long.toString(clientFactory.getEventBus().getHandlerCount(DeleteEvent.getType())));
		final RecordCardWidget.RecordCardPresenter presenter = this;
		Window.setTitle("Card");
		
		dispatcher.execute(
				new GetByIdAction(recordId),
				new AsyncCallback<GetByIdResult>()
				{
					public void onFailure(final Throwable cause)
					{
						Window.alert(Messages.SERVER_ERROR);
					}

					public void onSuccess(final GetByIdResult result)
					{
						PhoneRecord record = result.getRecord();
						if (record == null)
						{
							Window.alert(Messages.NONEXISTENT_RECORD);
							Utils.closeWindow();
							return;
						}
						
						display.setPresenter(presenter);
						display.setName(record.getName());
						display.setPhone(record.getPhone());
						
						panel.setWidget(display.asWidget());
					}
				});
	}

	@Override
	public void delete()
	{
		dispatcher.execute(
				new DeleteAction(recordId),
				new AsyncCallback<DeleteResult>()
				{
					public void onFailure(final Throwable cause)
					{
						Window.alert(Messages.SERVER_ERROR);
					}

					public void onSuccess(final DeleteResult result)
					{
						DeleteEvent event = new DeleteEvent(recordId);
						GlobalEventBus.eventBus.fireEvent(event);
						int cnt = GlobalEventBus.eventBus.getHandlerCount(DeleteEvent.getType());
						Window.alert(cnt + " ");
						Utils.closeWindow();
					}
				});
	}

	@Override
	public void edit()
	{
		//Utils.openFixWindow(RecordEditPlace.tokenPrefix, Long.toString(recordId), "Edit");
		clientFactory.getPlaceController().goTo(new RecordEditPlace(Long.toString(recordId)));
	}
}
