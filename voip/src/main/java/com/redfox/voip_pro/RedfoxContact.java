package com.redfox.voip_pro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneFriend.SubscribePolicy;
import org.linphone.mediastream.Log;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import com.redfox.ui.R;

public class RedfoxContact implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9015568163905205244L;

	private transient LinphoneFriend friend;
	private String fullName, firstName, lastName, androidId;
	private transient Uri photoUri, thumbnailUri;
	private List<RedfoxNumberOrAddress> addresses;
	private transient ArrayList<ContentProviderOperation> changesToCommit;
	private boolean hasSipAddress;
	
	public RedfoxContact() {
		addresses = new ArrayList<RedfoxNumberOrAddress>();
		androidId = null;
		thumbnailUri = null;
		photoUri = null;
		changesToCommit = new ArrayList<ContentProviderOperation>();
		hasSipAddress = false;
	}

	public void setFullName(String name) {
		fullName = name;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFirstNameAndLastName(String fn, String ln) {
		if (fn != null && fn.length() == 0 && ln != null && ln.length() == 0) return;
		
		if (isAndroidContact()) {
			if (firstName != null || lastName != null) {
				String select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";
				String[] args = new String[]{ getAndroidId() };
	
				changesToCommit.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
					.withSelection(select, args)
					.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(CommonDataKinds.StructuredName.GIVEN_NAME, fn)
					.withValue(CommonDataKinds.StructuredName.FAMILY_NAME, ln)
					.build()
				);
			} else {
				changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			        .withValue(CommonDataKinds.StructuredName.GIVEN_NAME, fn)
			        .withValue(CommonDataKinds.StructuredName.FAMILY_NAME, ln)
			        .build());
			}
		}
		
		firstName = fn;
		lastName = ln;
		if (firstName != null && lastName != null && firstName.length() > 0 && lastName.length() > 0) {
			fullName = firstName + " " + lastName;
		} else if (firstName != null && firstName.length() > 0) {
			fullName = firstName;
		} else if (lastName != null && lastName.length() > 0) {
			fullName = lastName;
		}
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public boolean hasPhoto() {
		return photoUri != null;
	}
	
	public void setPhotoUri(Uri uri) {
		photoUri = uri;
	}
	
	public Uri getPhotoUri() {
		return photoUri;
	}
	
	public void setThumbnailUri(Uri uri) {
		thumbnailUri = uri;
	}

	public Uri getThumbnailUri() {
		return thumbnailUri;
	}
	
	public void setPhoto(byte[] photo) {
		if (photo != null) {
			if (isAndroidContact()) {
				String rawContactId = findRawContactID(getAndroidId());
				if (rawContactId != null) {
					changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
						.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
						.withValue(CommonDataKinds.Photo.PHOTO, photo)
						.withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
						.build());
				} else {
					changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
						.withValue(CommonDataKinds.Photo.PHOTO, photo)
						.build());
				}
			} else if (isLinphoneFriend()) {
				//TODO: prepare photo changes in friend
			}
		}
	}

	public void addNumberOrAddress(RedfoxNumberOrAddress noa) {
		if (noa == null) return;
		if (noa.isSIPAddress()) {
			hasSipAddress = true;
		}
		addresses.add(noa);
	}

	public List<RedfoxNumberOrAddress> getNumbersOrAddresses() {
		return addresses;
	}

	public boolean hasAddress(String address) {
		for (RedfoxNumberOrAddress noa : getNumbersOrAddresses()) {
			if (noa.isSIPAddress()) {
				String value = noa.getValue();
				if (value.equals(address) || value.equals("sip:" + address)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasAddress() {
		return hasSipAddress;
	}
	
	public void removeNumberOrAddress(RedfoxNumberOrAddress noa) {
		if (noa != null && noa.getOldValue() != null) {
			if (isAndroidContact()) {
				String select;
				if (noa.isSIPAddress()) {
					select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE + "' AND " + CommonDataKinds.SipAddress.SIP_ADDRESS + "=?";
				} else {
					select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " + CommonDataKinds.Phone.NUMBER + "=?";
				}
				String[] args = new String[]{ getAndroidId(), noa.getOldValue() };
		
				changesToCommit.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
					.withSelection(select, args)
					.build());
			}
			
			if (isLinphoneFriend()) {
				if (!noa.getOldValue().startsWith("sip:")) {
					noa.setOldValue("sip:" + noa.getOldValue());
				}
				RedfoxNumberOrAddress toRemove = null;
				for (RedfoxNumberOrAddress address : addresses) {
					if (noa.getOldValue().equals(address.getValue())) {
						toRemove = address;
						break;
					}
				}
				if (toRemove != null) {
					addresses.remove(toRemove);
				}
			}
		}
	}
	
	public void addOrUpdateNumberOrAddress(RedfoxNumberOrAddress noa) {
		if (noa != null && noa.getValue() != null) {
			if (isAndroidContact()) {
				if (noa.getOldValue() == null) {
					ContentValues values = new ContentValues();
					if (noa.isSIPAddress()) {
						values.put(ContactsContract.Data.MIMETYPE, CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE);
				        values.put(CommonDataKinds.SipAddress.DATA, noa.getValue());
						values.put(CommonDataKinds.SipAddress.TYPE, CommonDataKinds.SipAddress.TYPE_CUSTOM);
						values.put(CommonDataKinds.SipAddress.LABEL, ContactsManager.getInstance().getString(R.string.addressbook_label));
					} else {
				        values.put(ContactsContract.Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
				        values.put(CommonDataKinds.Phone.NUMBER, noa.getValue());
				        values.put(CommonDataKinds.Phone.TYPE,  CommonDataKinds.Phone.TYPE_CUSTOM);
						values.put(CommonDataKinds.Phone.LABEL, ContactsManager.getInstance().getString(R.string.addressbook_label));
					}
					
					String rawContactId = findRawContactID(getAndroidId());
					if (rawContactId != null) {
						changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
								.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
						        .withValues(values)
						        .build());
					} else {
						changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					        .withValues(values)
					        .build());
					}
				} else {
					ContentValues values = new ContentValues();
					String select;
					String[] args = new String[] { getAndroidId(), noa.getOldValue() };
					
					if (noa.isSIPAddress()) {
						select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE + "' AND " + CommonDataKinds.SipAddress.SIP_ADDRESS + "=?";
						values.put(ContactsContract.Data.MIMETYPE, CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE);
				        values.put(CommonDataKinds.SipAddress.DATA, noa.getValue());
					} else {
						select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE +  "' AND " + CommonDataKinds.Phone.NUMBER + "=?";
				        values.put(ContactsContract.Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
				        values.put(CommonDataKinds.Phone.NUMBER, noa.getValue());
					}
					
					String rawContactId = findRawContactID(getAndroidId());
					if (rawContactId != null) {
						changesToCommit.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
							.withSelection(select, args)
					        .withValues(values)
					        .build());
					}
				}
			}
			if (isLinphoneFriend()) {
				if (!noa.getValue().startsWith("sip:")) {
					noa.setValue("sip:" + noa.getValue());
				}
				if (noa.getOldValue() != null) {
					if (!noa.getOldValue().startsWith("sip:")) {
						noa.setOldValue("sip:" + noa.getOldValue());
					}
					for (RedfoxNumberOrAddress address : addresses) {
						if (noa.getOldValue().equals(address.getValue())) {
							address.setValue(noa.getValue());
							break;
						}
					}
				} else {
					addresses.add(noa);
				}
			}
		}
	}
	
	public void setAndroidId(String id) {
		androidId = id;
	}

	public String getAndroidId() {
		return androidId;
	}
	
	public void save() {
		if (isAndroidContact() && ContactsManager.getInstance().hasContactsAccess() && changesToCommit.size() > 0) {
			try {
				ContactsManager.getInstance().getContentResolver().applyBatch(ContactsContract.AUTHORITY, changesToCommit);
			} catch (Exception e) {
				Log.e(e);
			} finally {
				changesToCommit = new ArrayList<ContentProviderOperation>();
			}
		}
		if (isLinphoneFriend()) {
			LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
			if (lc == null) return;
			
			friend.edit();
			friend.setName(fullName);
			//TODO: handle removal of all existing SIP addresses
			for (RedfoxNumberOrAddress address : addresses) {
				try {
					// Currently we only support 1 address / friend
					LinphoneAddress addr = lc.interpretUrl(address.getValue());
					if (addr != null) {
						friend.setAddress(addr);
					}
					break;
				} catch (LinphoneCoreException e) {
					Log.e(e);
				}
			}
			friend.done();
			
			if (friend.getAddress() != null) {
				if (lc.findFriendByAddress(friend.getAddress().asString()) == null) {
					try {
						lc.addFriend(friend);
						ContactsManager.getInstance().fetchContacts();
					} catch (LinphoneCoreException e) {
						Log.e(e);
					}
				}
			}
		}
	}

	public void delete() {
		if (isAndroidContact()) {
			String select = ContactsContract.Data.CONTACT_ID + " = ?";
			String[] args = new String[] { getAndroidId() };
			changesToCommit.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(select, args).build());
			save();
		}
		if (isLinphoneFriend()) {
			deleteFriend();
		}
	}
	
	public void deleteFriend() {
		if (friend != null) {
			RedfoxManager.getLcIfManagerNotDestroyedOrNull().removeFriend(friend);
		}
	}

	public void refresh() {
		addresses = new ArrayList<RedfoxNumberOrAddress>();
		hasSipAddress = false;
		
		if (!isAndroidContact() && isLinphoneFriend()) {
			fullName = friend.getName();
			thumbnailUri = null;
			photoUri = null;
			LinphoneAddress addr = friend.getAddress();
			if (addr != null) {
				addresses.add(new RedfoxNumberOrAddress(addr.asStringUriOnly(), true));
				hasSipAddress = true;
			}
		} else if (isAndroidContact()) {
			String id = getAndroidId();
			getContactNames(id);
			setThumbnailUri(getContactPictureUri(id));
			setPhotoUri(getContactPhotoUri(id));
			for (RedfoxNumberOrAddress noa : getAddressesAndNumbersForAndroidContact(id)) {
				addNumberOrAddress(noa);
			}
			
			if (friend == null) {
				friend = LinphoneCoreFactory.instance().createLinphoneFriend();
				friend.setRefKey(id);
				// Disable subscribes for now
				friend.enableSubscribes(false);
				friend.setIncSubscribePolicy(SubscribePolicy.SPDeny);
				friend.setName(fullName);
				if (hasSipAddress) {
					for (RedfoxNumberOrAddress noa : addresses) {
						if (noa.isSIPAddress()) {
							try {
								LinphoneAddress addr = RedfoxManager.getLc().interpretUrl(noa.getValue());
								if (addr != null) {
									friend.setAddress(addr);
								}
							} catch (LinphoneCoreException e) {
								Log.e(e);
							}
						}
					}
				}
				LinphoneCore lc = RedfoxManager.getLcIfManagerNotDestroyedOrNull();
				if (lc != null && friend.getAddress() != null) {
					try {
						lc.addFriend(friend);
					} catch (LinphoneCoreException e) {
						Log.e(e);
					}
				}
			}
		}
	}
	
	public boolean isAndroidContact() {
		return androidId != null;
	}
	
	public boolean isLinphoneFriend() {
		return friend != null;
	}

	public void setFriend(LinphoneFriend f) {
		friend = f;
	}

	public static RedfoxContact createContact() {
		if (ContactsManager.getInstance().hasContactsAccess()) {
			return createAndroidContact();
		}
		return createLinphoneFriend();
	}
	
	private Uri getContactPictureUri(String id) {
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
		return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	private Uri getContactPhotoUri(String id) {
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
		return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
	}
	
	private void getContactNames(String id) {
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		String[] proj = new String[]{ CommonDataKinds.StructuredName.GIVEN_NAME, CommonDataKinds.StructuredName.FAMILY_NAME, ContactsContract.Contacts.DISPLAY_NAME };
		String select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?";
		String[] args = new String[]{ id, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI, proj, select, args, null);
		if (c != null) {
			if (c.moveToFirst()) {
				firstName = c.getString(c.getColumnIndex(CommonDataKinds.StructuredName.GIVEN_NAME));
				lastName = c.getString(c.getColumnIndex(CommonDataKinds.StructuredName.FAMILY_NAME));
	        	fullName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			}
			c.close();
		}
	}
	
	private String findRawContactID(String id) {
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		String result = null;
		String[] projection = { ContactsContract.RawContacts._ID };
		
		String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
		Cursor c = resolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, new String[]{ id }, null);
		if (c != null) {
			if (c.moveToFirst()) {
				result = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
			}
			c.close();
		}
		return result;
	}
	
	private List<RedfoxNumberOrAddress> getAddressesAndNumbersForAndroidContact(String id) {
		List<RedfoxNumberOrAddress> result = new ArrayList<RedfoxNumberOrAddress>();
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		
		String select = ContactsContract.Data.CONTACT_ID + " =? AND (" + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?)";
		String[] projection = new String[] { CommonDataKinds.SipAddress.SIP_ADDRESS, ContactsContract.Data.MIMETYPE }; // PHONE_NUMBER == SIP_ADDRESS == "data1"...
		Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI, projection, select, new String[]{ id, CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE }, null);
		if (c != null) {
			while (c.moveToNext()) {
				String mime = c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE));
				if (mime != null && mime.length() > 0) {
					boolean found = false;
					boolean isSIP = false;
					if (mime.equals(CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)) {
						found = true;
						isSIP = true;
					} else if (mime.equals(CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
						found = true;
					}
					
					if (found) {
						String number = c.getString(c.getColumnIndex(CommonDataKinds.SipAddress.SIP_ADDRESS)); // PHONE_NUMBER == SIP_ADDRESS == "data1"...
						if (number != null && number.length() > 0) {
							if (isSIP && !number.startsWith("sip:")) {
								number = "sip:" + number;
							}
							result.add(new RedfoxNumberOrAddress(number, isSIP));
						}
					}
				}
			}
			c.close();
		}
		
		return result;
	}

	private static RedfoxContact createAndroidContact() {
		RedfoxContact contact = new RedfoxContact();
		contact.changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
	        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
	        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
	        .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)
	        .build());
		contact.setAndroidId("0");
		return contact;
	}

	private static RedfoxContact createLinphoneFriend() {
		RedfoxContact contact = new RedfoxContact();
		LinphoneFriend friend = LinphoneCoreFactory.instance().createLinphoneFriend();
		// Disable subscribes for now
		friend.enableSubscribes(false);
		friend.setIncSubscribePolicy(SubscribePolicy.SPDeny);
		contact.friend = friend;
		return contact;
	}
}
