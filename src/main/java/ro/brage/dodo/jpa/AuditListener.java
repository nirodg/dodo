/*******************************************************************************
 * Copyright 2018 Dorin Brage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package ro.brage.dodo.jpa;

import java.util.Date;
import javax.persistence.EntityListeners;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import ro.brage.dodo.http.auth.UserSession;

/**
 * The Audit Listener will update some of the Model's inherent fields.
 * 
 * @see EntityListeners
 * @author dorin
 */
public class AuditListener {
    
    @PrePersist
    public void setCreatedOn(Model entity) {
        entity.setCreatedBy(UserSession.get());
        entity.setUpdatedBy(UserSession.get());
        entity.setCreatedOn(new Date());
    }
    
    @PreUpdate
    public void setUpdatedOn(Model entity) {
        entity.setUpdatedBy(UserSession.get());
        entity.setUpdatedOn(new Date());
    }
    
}
