package com.techyourchance.testdrivendevelopment.exercise8;

import static com.techyourchance.testdrivendevelopment.exercise8.GetContactsUseCase.*;
import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GetContactsUseCaseTest {

    // region constants
    public static final String FILTER_TERM = "filter";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String IMAGE_URL = "imageUrl";
    public static final int AGE = 20;

    // endregion constants

    // region helper fields
    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpoint;
    @Mock
    Listener mListener1;
    @Mock
    Listener mListener2;
    @Captor
    ArgumentCaptor<List<Contact>> mAcListContact;

    // endregion helper fields

    GetContactsUseCase SUT;

    @Before
    public void setup() throws Exception {
        SUT = new GetContactsUseCase(getContactsHttpEndpoint);

        success();
    }

    // correct param passed to endpoint
    @Test
    public void getContacts_correctParamPassedToEndpoint() {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(getContactsHttpEndpoint).getContacts(ac.capture(), any(Callback.class));
        assertThat(ac.getValue(), is(FILTER_TERM));
    }

    // success - all observers notified with correct data
    @Test
    public void getContacts_success_allObserversNotifiedWithCorrectData() {
        // Arrange
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListener1).onGetContactsSucceeded(mAcListContact.capture());
        verify(mListener2).onGetContactsSucceeded(mAcListContact.capture());
        List<List<Contact>> captures = mAcListContact.getAllValues();
        List<Contact> list1 = captures.get(0);
        List<Contact> list2 = captures.get(1);
        assertThat(list1, is(getContacts()));
        assertThat(list2, is(getContacts()));
    }

    // success - unsubscribed observers not notified
    @Test
    public void getContacts_success_unsubscribedObserversNotNotified() {
        // Arrange
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.unregisterListener(mListener2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListener1).onGetContactsSucceeded(mAcListContact.capture());
        verifyNoMoreInteractions(mListener2);
        List<List<Contact>> captures = mAcListContact.getAllValues();
        List<Contact> list = captures.get(0);
        assertThat(list, is(getContacts()));
    }

    // general error - observers notified a failure
    @Test
    public void getContacts_generalError_observersNotifiedFailure() {
        // Arrange
        generalError();
        ArgumentCaptor<UseCaseFailReason> ac = ArgumentCaptor.forClass(UseCaseFailReason.class);
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListener1).onGetContactsFailed(ac.capture());
        verify(mListener2).onGetContactsFailed(ac.capture());
        List<UseCaseFailReason> captures = ac.getAllValues();
        assertThat(captures.get(0), is(UseCaseFailReason.GENERAL_ERROR));
        assertThat(captures.get(1), is(UseCaseFailReason.GENERAL_ERROR));
    }

    // network error - observers notified a network error specifically
    @Test
    public void getContacts_networkError_observersNotifiedNetworkErrorFailure() {
        // Arrange
        networkError();
        ArgumentCaptor<UseCaseFailReason> ac = ArgumentCaptor.forClass(UseCaseFailReason.class);
        // Act
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListener1).onGetContactsFailed(ac.capture());
        verify(mListener2).onGetContactsFailed(ac.capture());
        List<UseCaseFailReason> captures = ac.getAllValues();
        assertThat(captures.get(0), is(UseCaseFailReason.NETWORK_ERROR));
        assertThat(captures.get(1), is(UseCaseFailReason.NETWORK_ERROR));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);

                return null;
            }
        }).when(getContactsHttpEndpoint).getContacts(anyString(), any(Callback.class));
    }

    // region helper methods
    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);

                return null;
            }
        }).when(getContactsHttpEndpoint).getContacts(anyString(), any(Callback.class));
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactSchemas());

                return null;
            }
        }).when(getContactsHttpEndpoint).getContacts(anyString(), any(Callback.class));
    }

    private List<ContactSchema> getContactSchemas() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, FULL_NAME, PHONE_NUMBER, IMAGE_URL, AGE));
        return contactSchemas;
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, PHONE_NUMBER));
        return contacts;
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}