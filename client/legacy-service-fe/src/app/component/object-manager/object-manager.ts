import { CommonModule } from '@angular/common';
import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, FFDResult, ObjectDescription, ObjectDescriptionDetail, ObjectManagerListFilterParams } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { FormsModule } from '@angular/forms';
class DSPOBJDResult {
  library: string = '';
}

@Component({
  selector: 'app-object-manager',
  imports: [FormsModule],
  templateUrl: './object-manager.html',
  styleUrl: './object-manager.css',
})
export class ObjectManager {

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }

  m_object_list: WritableSignal<ObjectDescription[]> = signal([]);

  m_objectDescriptionDetail: WritableSignal<ObjectDescriptionDetail> = signal(new ObjectDescriptionDetail());
  m_ffd: WritableSignal<FFDResult> = signal(new FFDResult());

  get objectManagerListFilterParams(): ObjectManagerListFilterParams {
    return this.bkService.g_objectManagerListFilterParams;
  }
  set objectManagerListFilterParams(value: ObjectManagerListFilterParams) {
    this.bkService.g_objectManagerListFilterParams = value;
  }

  getObjetcList() {
    this.m_object_list.set([
      {
        library: 'no data found !!!',
        name: '',
        type: '',
        attribute: '',
        description: ''
      }
    ]);

    console.log('[0]-getObjetcList');
    this.bkService.getWRKOBJ(this.objectManagerListFilterParams.libreria, this.objectManagerListFilterParams.nome, this.objectManagerListFilterParams.tipo).subscribe(
      data => {
        console.log(data);
        this.m_object_list.set(data);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }

    );

    // console.log('[1]-getObjetcList', x);
    return this.m_object_list;
    // this.subscriptionToolBarComponents.toArray().forEach(val => val.getAmbienteStatus());
  }
  clean_filter() {
    console.log('clean_filter');
    this.objectManagerListFilterParams.libreria = '*LIBL';
    this.objectManagerListFilterParams.nome = '';
    this.objectManagerListFilterParams.tipo = '';
  }

  showDetail(o: ObjectDescription) {
    this.bkService.getDSPOBJD(o.library, o.name, o.type).subscribe(
      data => {
        this.m_objectDescriptionDetail.set(data);
        console.log(data);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }

    );
    console.log(o);
  }

  showFFD(o: ObjectDescription) {
    console.log('FFD:', o);
    this.bkService.getFFD(o.library, o.name).subscribe(
      data => {
        this.m_ffd.set(data);
        console.log(data);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta ffd');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta ffd');
      }
    );
  }

  getFFDLen(): number | undefined {
    let sizeFFD: number = 0;
    if (this.m_ffd() !== null && this.m_ffd().fields !== null) {
      this.m_ffd().fields?.forEach((element, index) => {
        if (element.fieldType === 'A' || element.fieldType === 'S')
          sizeFFD += element.fieldLength;
        else if (element.fieldType === 'T')
          sizeFFD += 8;
        else if (element.fieldType === 'L')
          sizeFFD += 10;
        else if (element.fieldType === 'P')
          sizeFFD += (element.fieldLength / 2) + 1;
        console.log('[1]', element, index, sizeFFD);
        sizeFFD = Math.floor(sizeFFD);
        console.log('[2]', element, index, sizeFFD);
      });
    }
    return sizeFFD;
  }

  onKeydown(event: any) {
    if (event.code.indexOf('Enter') > -1)
      this.getObjetcList();
  }
}
