package com.techyourchance.testdrivendevelopment.exercise8;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class GetContactsUseCase {

    public interface Listener {
        public void onGetContactsSucceeded(List<Contact> contactItems);

        void onGetContactsFailed(UseCaseFailReason reason);
    }

    public enum UseCaseFailReason {
        NETWORK_ERROR,
        GENERAL_ERROR
    }

    private final GetContactsHttpEndpoint getContactsHttpEndpoint;

    private final List<Listener> mListeners = new ArrayList<>();

    public GetContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void getContacts(String filterTerm) {
        getContactsHttpEndpoint.getContacts(filterTerm, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemas) {
                notifySucceeded(contactSchemas);
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR:
                        notifyFailed(UseCaseFailReason.GENERAL_ERROR);
                        break;
                    case NETWORK_ERROR:
                        notifyFailed(UseCaseFailReason.NETWORK_ERROR);
                        break;
                }
            }
        });

    }

    private void notifyFailed(UseCaseFailReason reason) {
        for (Listener listener : mListeners) {
            listener.onGetContactsFailed(reason);
        }
    }

    private void notifySucceeded(List<ContactSchema> contactSchemas) {
        for (Listener listener : mListeners) {
            listener.onGetContactsSucceeded(contactsFromContactSchemas(contactSchemas));
        }
    }

    private List<Contact> contactsFromContactSchemas(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema contactSchema : contactSchemas) {
            contacts.add(
                    new Contact(
                            contactSchema.getId(),
                            contactSchema.getFullName(),
                            contactSchema.getFullPhoneNumber()
                    )
            );
        }
        return contacts;
    }

    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }

}
